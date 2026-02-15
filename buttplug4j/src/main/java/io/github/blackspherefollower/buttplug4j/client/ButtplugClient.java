package io.github.blackspherefollower.buttplug4j.client;

import io.github.blackspherefollower.buttplug4j.protocol.ButtplugConsts;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugDeviceMessage;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugJsonMessageParser;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugMessage;
import io.github.blackspherefollower.buttplug4j.protocol.messages.*;
import io.github.blackspherefollower.buttplug4j.protocol.messages.Error;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ButtplugClient is the abstract class containing the bulk of the logic for communicating with the Buttplug.io sever
 * Intiface Central.
 * <p>
 * The transport logic is not in this package, as the various websocket libraries have differing
 * requirements that will not fit all use-cases. In general, the Java8 compatible Jetty Websocket Client connector will
 * meet the needs of most users: see <a href="https://mvnrepository.com/artifact/io.github.blackspherefollower/buttplug4j.connectors.jetty.websocket.client">https://mvnrepository.com/artifact/io.github.blackspherefollower/buttplug4j.connectors.jetty.websocket.client</a>
 */
public abstract class ButtplugClient {
    static final int MAX_DISCONNECT_MESSAGE_TRYS = 3;
    private final ButtplugJsonMessageParser parser;
    private final String clientName;
    private final ConcurrentHashMap<Integer, CompletableFuture<ButtplugMessage>> waitingMsgs = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, ButtplugClientDevice> devices = new ConcurrentHashMap<>();
    private final AtomicInteger msgId = new AtomicInteger(1);
    private final Object sendLock = new Object();
    private ConnectionState connectionState = ConnectionState.DISCONNECTED;
    private Timer pingTimer;
    private IDeviceAddedEvent deviceAddedHandler;
    private IDeviceChangedEvent deviceChangedHandler;
    private IDeviceRemovedEvent deviceRemovedHandler;
    private IScanningEvent scanningFinishedHandler;
    private IErrorEvent errorHandler;
    private IInputEvent inputHandler;
    private IConnectedEvent onConnectedHandler;

    public ButtplugClient(final String aClientName) {
        parser = new ButtplugJsonMessageParser();
        clientName = aClientName;
    }

    protected final ButtplugJsonMessageParser getParser() {
        return parser;
    }

    public final ConnectionState getConnectionState() {
        return connectionState;
    }

    protected final void setConnectionState(final ConnectionState connectionState) {
        this.connectionState = connectionState;
    }

    public final boolean isConnected() {
        return connectionState == ConnectionState.CONNECTED;
    }

    public final int getNextMsgId() {
        return msgId.getAndIncrement();
    }

    public final void onMessage(final List<ButtplugMessage> msgs) {
        for (ButtplugMessage msg : msgs) {
            if (msg instanceof DeviceList) {
                ArrayList<Integer> curDevs = new ArrayList<Integer>();
                for (Map.Entry<Integer, ButtplugClientDevice> dev : devices.entrySet()) {
                    curDevs.add(dev.getKey());
                }

                HashMap<Integer, Device> newDevices = ((DeviceList) msg).getDevices();
                ArrayList<Integer> newDevs = new ArrayList<Integer>(newDevices.keySet());

                curDevs.sort(Integer::compare);
                newDevs.sort(Integer::compare);

                int curIdx = 0;
                int newIdx = 0;
                while (curIdx < curDevs.size() && newIdx < newDevs.size()) {
                    int compare = curDevs.get(curIdx) - newDevs.get(newIdx);

                    if (compare > 0) {
                        devices.put(newDevs.get(newIdx), new ButtplugClientDevice(this, newDevices.get(newDevs.get(newIdx))));
                        if (getDeviceAddedHandler() != null) {
                            getDeviceAddedHandler().deviceAdded(devices.get(newDevs.get(newIdx)));
                        }
                        newIdx++;
                    } else if (compare < 0) {
                        devices.remove(curDevs.get(curIdx));
                        if (getDeviceRemovedHandler() != null) {
                            getDeviceRemovedHandler().deviceRemoved(curDevs.get(curIdx));
                        }
                        curIdx++;
                    } else {
                        // Same index, diff to see if updated
                        ButtplugClientDevice newDev = new ButtplugClientDevice(this, newDevices.get(newDevs.get(newIdx)));
                        if (!newDev.equals(devices.get(curDevs.get(curIdx)))) {
                            devices.put(newDevs.get(newIdx), newDev);
                            if (getDeviceChanged() != null) {
                                getDeviceChanged().deviceChanged(devices.get(newDevs.get(newIdx)));
                            }
                        }
                        newIdx++;
                        curIdx++;
                    }
                }

                while (curIdx < curDevs.size()) {
                    devices.remove(curDevs.get(curIdx));
                    if (getDeviceRemovedHandler() != null) {
                        getDeviceRemovedHandler().deviceRemoved(curDevs.get(curIdx));
                    }
                    curIdx++;
                }
                while (newIdx < newDevs.size()) {
                    devices.put(newDevs.get(newIdx), new ButtplugClientDevice(this, newDevices.get(newDevs.get(newIdx))));
                    if (getDeviceAddedHandler() != null) {
                        getDeviceAddedHandler().deviceAdded(devices.get(newDevs.get(newIdx)));
                    }
                    newIdx++;
                }
            }

            if (msg.getId() > 0) {
                CompletableFuture<ButtplugMessage> val = waitingMsgs.remove(msg.getId());
                if (val != null) {
                    val.complete(msg);
                    continue;
                }
            }

            if (msg instanceof ScanningFinished) {
                if (getScanningFinishedHandler() != null) {
                    getScanningFinishedHandler().scanningFinished();
                }
            } else if (msg instanceof Error) {
                if (getErrorHandler() != null) {
                    getErrorHandler().errorReceived((Error) msg);
                }
            } else if (msg instanceof InputReading) {
                if (getInputHandler() != null) {
                    getInputHandler().inputEvent((InputReading) msg);
                }
            }
        }
    }

    protected final void doHandshake() {
        waitingMsgs.clear();
        devices.clear();
        msgId.set(1);

        try {
            ButtplugMessage res = sendMessage(new RequestServerInfo(clientName, getNextMsgId())).get(1, TimeUnit.MINUTES);
            if (res instanceof ServerInfo) {
                if (((ServerInfo) res).getMaxPingTime() > 0) {
                    pingTimer = new Timer("pingTimer", true);
                    pingTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                onPingTimer();
                            } catch (Exception e) {
                                if ( getErrorHandler() != null) {
                                    getErrorHandler().errorReceived(new Error(new ButtplugClientException(e)));
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, 0, Math.round(((double) ((ServerInfo) res).getMaxPingTime()) / 2));
                }

                // Populate already connected devices
                requestDeviceList();

            } else if (res instanceof Error) {
                throw new ButtplugClientException(((Error) res).getErrorMessage());
            } else {
                throw new ButtplugClientException("Unexpected message returned: " + res.getClass().getName());
            }
        } catch (ButtplugClientException | InterruptedException | ExecutionException | TimeoutException e) {
            if (getErrorHandler() != null) {
                getErrorHandler().errorReceived(new Error(new ButtplugClientException(e)));
            } else {
                e.printStackTrace();
            }
        }

        connectionState = ConnectionState.CONNECTED;

        if (getOnConnectedHandler() != null) {
            getOnConnectedHandler().onConnected(this);
        }

    }

    private void onPingTimer() throws ButtplugClientException, ExecutionException, InterruptedException, TimeoutException {
        try {
            ButtplugMessage msg = sendMessage(new Ping(msgId.incrementAndGet())).get(1, TimeUnit.MINUTES);
            if (msg instanceof Error) {
                throw new ButtplugClientException(((Error) msg).getErrorMessage());
            }
        } catch (ButtplugClientException | InterruptedException | ExecutionException | TimeoutException e) {
            disconnect();
            throw e;
        }
    }

    public final void requestDeviceList() throws ButtplugClientException, ExecutionException, InterruptedException, TimeoutException {
        Object res = sendMessage(new RequestDeviceList(msgId.incrementAndGet())).get(1, TimeUnit.MINUTES);
        if (res instanceof Error) {
            throw new ButtplugClientException(((Error) res).getErrorMessage());
        }
    }

    public final List<ButtplugClientDevice> getDevices() {
        return new ArrayList<>(this.devices.values());
    }

    public final boolean startScanning() throws ExecutionException, InterruptedException, IOException, TimeoutException {
        return waitForOk(startScanningAsync());
    }

    public final Future<ButtplugMessage> startScanningAsync() {
        return sendMessage(new StartScanning(msgId.incrementAndGet()));
    }

    public final boolean stopScanning() throws ExecutionException, InterruptedException, TimeoutException {
        return waitForOk(stopScanningAsync());
    }

    public final Future<ButtplugMessage> stopScanningAsync() {
        return sendMessage(new StopScanning(msgId.incrementAndGet()));
    }

    public final boolean stopAllDevices() throws ExecutionException, InterruptedException, IOException, TimeoutException {
        return waitForOk(stopAllDevicesAsync());
    }

    public final boolean stopAllDevices(final boolean inputs, final boolean outputs) throws ExecutionException, InterruptedException, IOException, TimeoutException {
        return waitForOk(stopAllDevicesAsync(inputs, outputs));
    }

    public final Future<ButtplugMessage> stopAllDevicesAsync() {
        return sendMessage(new StopCmd(getNextMsgId()));
    }

    public final Future<ButtplugMessage> stopAllDevicesAsync(final boolean inputs, final boolean outputs) {
        return sendMessage(new StopCmd(getNextMsgId(), inputs, outputs));
    }

    public final CompletableFuture<ButtplugMessage> sendDeviceMessage(
            final ButtplugClientDevice device, final ButtplugDeviceMessage deviceMsg) {
        ButtplugClientDevice dev = devices.get(device.getDeviceIndex());
        if (dev != null) {
            deviceMsg.setDeviceIndex(device.getDeviceIndex());
            deviceMsg.setId(msgId.incrementAndGet());
            return sendMessage(deviceMsg);
        } else {
            return CompletableFuture.completedFuture(new Error("Device not available.",
                    Error.ErrorClass.ERROR_DEVICE, ButtplugConsts.SYSTEM_MSG_ID));
        }
    }

    protected final boolean waitForOk(final Future<ButtplugMessage> msg)
            throws ExecutionException, InterruptedException, TimeoutException {
        return msg.get(1, TimeUnit.MINUTES) instanceof Ok;
    }

    protected abstract CompletableFuture<ButtplugMessage> sendMessage(ButtplugMessage msg);

    public final IDeviceAddedEvent getDeviceAddedHandler() {
        return deviceAddedHandler;
    }

    public final void setDeviceAddedHandler(final IDeviceAddedEvent deviceAddedHandler) {
        this.deviceAddedHandler = deviceAddedHandler;
    }

    public final IDeviceChangedEvent getDeviceChanged() {
        return deviceChangedHandler;
    }

    public final void setDeviceChangedHandler(final IDeviceChangedEvent deviceChangedHandler) {
        this.deviceChangedHandler = deviceChangedHandler;
    }

    public final IDeviceRemovedEvent getDeviceRemovedHandler() {
        return deviceRemovedHandler;
    }

    public final void setDeviceRemovedHandler(final IDeviceRemovedEvent deviceRemovedHandler) {
        this.deviceRemovedHandler = deviceRemovedHandler;
    }

    public final IScanningEvent getScanningFinishedHandler() {
        return scanningFinishedHandler;
    }

    public final void setScanningFinishedHandler(final IScanningEvent scanningFinishedHandler) {
        this.scanningFinishedHandler = scanningFinishedHandler;
    }

    public final IErrorEvent getErrorHandler() {
        return errorHandler;
    }

    public final void setErrorHandler(final IErrorEvent errorReceivedHandler) {
        this.errorHandler = errorReceivedHandler;
    }

    public final IInputEvent getInputHandler() {
        return inputHandler;
    }

    public final void setInputHandler(final IInputEvent inputHandler) {
        this.inputHandler = inputHandler;
    }

    public final IConnectedEvent getOnConnectedHandler() {
        return onConnectedHandler;
    }

    public final void setOnConnected(final IConnectedEvent onConnected) {
        this.onConnectedHandler = onConnected;
    }

    protected abstract void cleanup();

    public final void disconnect() {
        if (pingTimer != null) {
            pingTimer.cancel();
            pingTimer = null;
        }

        cleanup();

        int max = MAX_DISCONNECT_MESSAGE_TRYS;
        while (max-- > 0 && !waitingMsgs.isEmpty()) {
            for (int waitMmsgId : waitingMsgs.keySet()) {
                CompletableFuture<ButtplugMessage> val = waitingMsgs.remove(waitMmsgId);
                if (val != null) {
                    val.complete(new Error("Connection closed!",
                            Error.ErrorClass.ERROR_UNKNOWN, ButtplugConsts.SYSTEM_MSG_ID));
                }
            }
        }

        msgId.set(1);
    }

    protected final CompletableFuture<ButtplugMessage> scheduleWait(final int id,
                                                                    final CompletableFuture<ButtplugMessage> promise) {
        waitingMsgs.put(id, promise);
        return promise;
    }

    public enum ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }
}
