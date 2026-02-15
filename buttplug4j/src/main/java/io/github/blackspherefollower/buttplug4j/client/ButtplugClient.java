package io.github.blackspherefollower.buttplug4j.client;

import io.github.blackspherefollower.buttplug4j.protocol.ButtplugConsts;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugDeviceMessage;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugJsonMessageParser;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugMessage;
import io.github.blackspherefollower.buttplug4j.protocol.messages.Device;
import io.github.blackspherefollower.buttplug4j.protocol.messages.DeviceList;
import io.github.blackspherefollower.buttplug4j.protocol.messages.Error;
import io.github.blackspherefollower.buttplug4j.protocol.messages.InputReading;
import io.github.blackspherefollower.buttplug4j.protocol.messages.Ok;
import io.github.blackspherefollower.buttplug4j.protocol.messages.Ping;
import io.github.blackspherefollower.buttplug4j.protocol.messages.RequestDeviceList;
import io.github.blackspherefollower.buttplug4j.protocol.messages.RequestServerInfo;
import io.github.blackspherefollower.buttplug4j.protocol.messages.ScanningFinished;
import io.github.blackspherefollower.buttplug4j.protocol.messages.ServerInfo;
import io.github.blackspherefollower.buttplug4j.protocol.messages.StartScanning;
import io.github.blackspherefollower.buttplug4j.protocol.messages.StopCmd;
import io.github.blackspherefollower.buttplug4j.protocol.messages.StopScanning;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ButtplugClient is the abstract class containing the bulk of the logic for communicating with the Buttplug.io sever
 * Intiface Central.
 * <p>
 * The transport logic is not in this package, as the various websocket libraries have differing
 * requirements that will not fit all use-cases. In general, the Java8 compatible Jetty Websocket Client connector will
 * meet the needs of most users: see <a href="https://tinyurl.com/buttplug4j">tinyurl.com/buttplug4j</a>
 */
public abstract class ButtplugClient {
    /**
     * Max number of tries to disconnect.
     */
    static final int MAX_DISCONNECT_MESSAGE_TRYS = 3;
    /**
     * JSON parser.
     */
    private final ButtplugJsonMessageParser parser;
    /**
     * Client name.
     */
    private final String clientName;
    /**
     * Waiting messages.
     */
    private final ConcurrentHashMap<Integer, CompletableFuture<ButtplugMessage>> waitingMsgs =
            new ConcurrentHashMap<>();
    /**
     * Connected devices.
     */
    private final ConcurrentHashMap<Integer, ButtplugClientDevice> devices = new ConcurrentHashMap<>();
    /**
     * Message ID counter.
     */
    private final AtomicInteger msgId = new AtomicInteger(1);
    /**
     * Send lock.
     */
    private final Object sendLock = new Object();
    /**
     * Connection state.
     */
    private ConnectionState connectionState = ConnectionState.DISCONNECTED;
    /**
     * Ping timer.
     */
    private Timer pingTimer;
    /**
     * Device added handler.
     */
    private IDeviceAddedEvent deviceAddedHandler;
    /**
     * Device changed handler.
     */
    private IDeviceChangedEvent deviceChangedHandler;
    /**
     * Device removed handler.
     */
    private IDeviceRemovedEvent deviceRemovedHandler;
    /**
     * Scanning finished handler.
     */
    private IScanningEvent scanningFinishedHandler;
    /**
     * Error handler.
     */
    private IErrorEvent errorHandler;
    /**
     * Input handler.
     */
    private IInputEvent inputHandler;
    /**
     * Connected handler.
     */
    private IConnectedEvent onConnectedHandler;

    /**
     * Constructor.
     *
     * @param aClientName client name
     */
    public ButtplugClient(final String aClientName) {
        parser = new ButtplugJsonMessageParser();
        clientName = aClientName;
    }

    /**
     * Get the JSON parser.
     *
     * @return parser
     */
    protected final ButtplugJsonMessageParser getParser() {
        return parser;
    }

    /**
     * Get the connection state.
     *
     * @return connection state
     */
    public final ConnectionState getConnectionState() {
        return connectionState;
    }

    /**
     * Set the connection state.
     *
     * @param connectionState connection state
     */
    protected final void setConnectionState(final ConnectionState connectionState) {
        this.connectionState = connectionState;
    }

    /**
     * Check if the client is connected.
     *
     * @return true if connected
     */
    public final boolean isConnected() {
        return connectionState == ConnectionState.CONNECTED;
    }

    /**
     * Get the next message ID.
     *
     * @return message ID
     */
    public final int getNextMsgId() {
        return msgId.getAndIncrement();
    }

    /**
     * Called when a list of messages is received.
     *
     * @param msgs messages
     */
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
                        devices.put(newDevs.get(newIdx),
                                new ButtplugClientDevice(this, newDevices.get(newDevs.get(newIdx))));
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
                        ButtplugClientDevice newDev =
                                new ButtplugClientDevice(this, newDevices.get(newDevs.get(newIdx)));
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
                    devices.put(newDevs.get(newIdx),
                            new ButtplugClientDevice(this, newDevices.get(newDevs.get(newIdx))));
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

    /**
     * Perform the handshake with the server.
     */
    protected final void doHandshake() {
        waitingMsgs.clear();
        devices.clear();
        msgId.set(1);

        try {
            ButtplugMessage res = sendMessage(new RequestServerInfo(clientName, getNextMsgId()))
                    .get(1, TimeUnit.MINUTES);
            if (res instanceof ServerInfo) {
                if (((ServerInfo) res).getMaxPingTime() > 0) {
                    pingTimer = new Timer("pingTimer", true);
                    pingTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                onPingTimer();
                            } catch (Exception e) {
                                if (getErrorHandler() != null) {
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

    /**
     * Called when the ping timer expires.
     *
     * @throws ButtplugClientException if an error occurs
     * @throws ExecutionException      if an error occurs
     * @throws InterruptedException    if an error occurs
     * @throws TimeoutException        if an error occurs
     */
    private void onPingTimer() throws ButtplugClientException, ExecutionException,
            InterruptedException, TimeoutException {
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

    /**
     * Request the device list from the server.
     *
     * @throws ButtplugClientException if an error occurs
     * @throws ExecutionException      if an error occurs
     * @throws InterruptedException    if an error occurs
     * @throws TimeoutException        if an error occurs
     */
    public final void requestDeviceList() throws ButtplugClientException, ExecutionException,
            InterruptedException, TimeoutException {
        Object res = sendMessage(new RequestDeviceList(msgId.incrementAndGet())).get(1, TimeUnit.MINUTES);
        if (res instanceof Error) {
            throw new ButtplugClientException(((Error) res).getErrorMessage());
        }
    }

    /**
     * Get the list of connected devices.
     *
     * @return devices
     */
    public final List<ButtplugClientDevice> getDevices() {
        return new ArrayList<>(this.devices.values());
    }

    /**
     * Start scanning for devices.
     *
     * @return true if successful
     * @throws ExecutionException   if an error occurs
     * @throws InterruptedException if an error occurs
     * @throws IOException          if an error occurs
     * @throws TimeoutException     if an error occurs
     */
    public final boolean startScanning() throws ExecutionException, InterruptedException,
            IOException, TimeoutException {
        return waitForOk(startScanningAsync());
    }

    /**
     * Start scanning for devices asynchronously.
     *
     * @return future
     */
    public final Future<ButtplugMessage> startScanningAsync() {
        return sendMessage(new StartScanning(msgId.incrementAndGet()));
    }

    /**
     * Stop scanning for devices.
     *
     * @return true if successful
     * @throws ExecutionException   if an error occurs
     * @throws InterruptedException if an error occurs
     * @throws TimeoutException     if an error occurs
     */
    public final boolean stopScanning() throws ExecutionException, InterruptedException, TimeoutException {
        return waitForOk(stopScanningAsync());
    }

    /**
     * Stop scanning for devices asynchronously.
     *
     * @return future
     */
    public final Future<ButtplugMessage> stopScanningAsync() {
        return sendMessage(new StopScanning(msgId.incrementAndGet()));
    }

    /**
     * Stop all devices.
     *
     * @return true if successful
     * @throws ExecutionException   if an error occurs
     * @throws InterruptedException if an error occurs
     * @throws IOException          if an error occurs
     * @throws TimeoutException     if an error occurs
     */
    public final boolean stopAllDevices() throws ExecutionException, InterruptedException,
            IOException, TimeoutException {
        return waitForOk(stopAllDevicesAsync());
    }

    /**
     * Stop all devices.
     *
     * @param inputs  stop inputs
     * @param outputs stop outputs
     * @return true if successful
     * @throws ExecutionException   if an error occurs
     * @throws InterruptedException if an error occurs
     * @throws IOException          if an error occurs
     * @throws TimeoutException     if an error occurs
     */
    public final boolean stopAllDevices(final boolean inputs, final boolean outputs)
            throws ExecutionException, InterruptedException, IOException, TimeoutException {
        return waitForOk(stopAllDevicesAsync(inputs, outputs));
    }

    /**
     * Stop all devices asynchronously.
     *
     * @return future
     */
    public final Future<ButtplugMessage> stopAllDevicesAsync() {
        return sendMessage(new StopCmd(getNextMsgId()));
    }

    /**
     * Stop all devices asynchronously.
     *
     * @param inputs  stop inputs
     * @param outputs stop outputs
     * @return future
     */
    public final Future<ButtplugMessage> stopAllDevicesAsync(final boolean inputs, final boolean outputs) {
        return sendMessage(new StopCmd(getNextMsgId(), inputs, outputs));
    }

    /**
     * Send a device message.
     *
     * @param device    device
     * @param deviceMsg message
     * @return future
     */
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

    /**
     * Wait for an Ok message.
     *
     * @param msg message future
     * @return true if Ok
     * @throws ExecutionException   if an error occurs
     * @throws InterruptedException if an error occurs
     * @throws TimeoutException     if an error occurs
     */
    protected final boolean waitForOk(final Future<ButtplugMessage> msg)
            throws ExecutionException, InterruptedException, TimeoutException {
        return msg.get(1, TimeUnit.MINUTES) instanceof Ok;
    }

    /**
     * Send a message.
     *
     * @param msg message
     * @return future
     */
    protected abstract CompletableFuture<ButtplugMessage> sendMessage(ButtplugMessage msg);

    /**
     * Get the device added handler.
     *
     * @return handler
     */
    public final IDeviceAddedEvent getDeviceAddedHandler() {
        return deviceAddedHandler;
    }

    /**
     * Set the device added handler.
     *
     * @param deviceAddedHandler handler
     */
    public final void setDeviceAddedHandler(final IDeviceAddedEvent deviceAddedHandler) {
        this.deviceAddedHandler = deviceAddedHandler;
    }

    /**
     * Get the device changed handler.
     *
     * @return handler
     */
    public final IDeviceChangedEvent getDeviceChanged() {
        return deviceChangedHandler;
    }

    /**
     * Set the device changed handler.
     *
     * @param deviceChangedHandler handler
     */
    public final void setDeviceChangedHandler(final IDeviceChangedEvent deviceChangedHandler) {
        this.deviceChangedHandler = deviceChangedHandler;
    }

    /**
     * Get the device removed handler.
     *
     * @return handler
     */
    public final IDeviceRemovedEvent getDeviceRemovedHandler() {
        return deviceRemovedHandler;
    }

    /**
     * Set the device removed handler.
     *
     * @param deviceRemovedHandler handler
     */
    public final void setDeviceRemovedHandler(final IDeviceRemovedEvent deviceRemovedHandler) {
        this.deviceRemovedHandler = deviceRemovedHandler;
    }

    /**
     * Get the scanning finished handler.
     *
     * @return handler
     */
    public final IScanningEvent getScanningFinishedHandler() {
        return scanningFinishedHandler;
    }

    /**
     * Set the scanning finished handler.
     *
     * @param scanningFinishedHandler handler
     */
    public final void setScanningFinishedHandler(final IScanningEvent scanningFinishedHandler) {
        this.scanningFinishedHandler = scanningFinishedHandler;
    }

    /**
     * Get the error handler.
     *
     * @return handler
     */
    public final IErrorEvent getErrorHandler() {
        return errorHandler;
    }

    /**
     * Set the error handler.
     *
     * @param errorReceivedHandler handler
     */
    public final void setErrorHandler(final IErrorEvent errorReceivedHandler) {
        this.errorHandler = errorReceivedHandler;
    }

    /**
     * Get the input handler.
     *
     * @return handler
     */
    public final IInputEvent getInputHandler() {
        return inputHandler;
    }

    /**
     * Set the input handler.
     *
     * @param inputHandler handler
     */
    public final void setInputHandler(final IInputEvent inputHandler) {
        this.inputHandler = inputHandler;
    }

    /**
     * Get the connected handler.
     *
     * @return handler
     */
    public final IConnectedEvent getOnConnectedHandler() {
        return onConnectedHandler;
    }

    /**
     * Set the connected handler.
     *
     * @param onConnected handler
     */
    public final void setOnConnected(final IConnectedEvent onConnected) {
        this.onConnectedHandler = onConnected;
    }

    /**
     * Cleanup resources.
     */
    protected abstract void cleanup();

    /**
     * Disconnect the client.
     */
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

    /**
     * Schedule a wait for a message response.
     *
     * @param id      message ID
     * @param promise promise
     * @return future
     */
    protected final CompletableFuture<ButtplugMessage> scheduleWait(final int id,
                                                                    final CompletableFuture<ButtplugMessage> promise) {
        waitingMsgs.put(id, promise);
        return promise;
    }

    /**
     * Connection state enum.
     */
    public enum ConnectionState {
        /**
         * Disconnected.
         */
        DISCONNECTED,
        /**
         * Connecting.
         */
        CONNECTING,
        /**
         * Connected.
         */
        CONNECTED
    }
}
