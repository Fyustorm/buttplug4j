package io.github.blackspherefollower.buttplug4j.client;

import io.github.blackspherefollower.buttplug4j.ButtplugException;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugMessage;
import io.github.blackspherefollower.buttplug4j.protocol.messages.Device;
import io.github.blackspherefollower.buttplug4j.protocol.messages.DeviceFeature;
import io.github.blackspherefollower.buttplug4j.protocol.messages.InputCommandType;
import io.github.blackspherefollower.buttplug4j.protocol.messages.InputCmd;
import io.github.blackspherefollower.buttplug4j.protocol.messages.OutputCmd;
import io.github.blackspherefollower.buttplug4j.protocol.messages.StopCmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * ButtplugClientDevice.
 */
public final class ButtplugClientDevice {

    /**
     * Buttplug client.
     */
    private final ButtplugClient client;
    /**
     * Device index.
     */
    private final int deviceIndex;
    /**
     * Device name.
     */
    private final String deviceName;
    /**
     * Device display name.
     */
    private final String deviceDisplayName;
    /**
     * Device features.
     */
    private final HashMap<Integer, ButtplugClientDeviceFeature> deviceFeatures = new HashMap<>();
    /**
     * Device message timing gap.
     */
    private Integer deviceMessageTimingGap;

    /**
     * Constructor.
     *
     * @param bpClient client
     * @param device   device
     */
    public ButtplugClientDevice(final ButtplugClient bpClient, final Device device) {
        this.client = bpClient;
        this.deviceIndex = device.getDeviceIndex();
        this.deviceName = device.getDeviceName();
        this.deviceDisplayName = device.getDeviceDisplayName() != null
                && !device.getDeviceDisplayName().isEmpty()
                ? device.getDeviceDisplayName() : device.getDeviceName();
        this.deviceMessageTimingGap = device.getDeviceMessageTimingGap();
        if (device.getDeviceFeatures() != null) {
            for (Map.Entry<Integer, DeviceFeature> feature : device.getDeviceFeatures().entrySet()) {
                this.deviceFeatures.put(feature.getKey(), new ButtplugClientDeviceFeature(this, feature.getValue()));
            }
        }
    }

    /**
     * Send a stop device command.
     *
     * @return future
     */
    public Future<ButtplugMessage> sendStopDeviceCmd() {
        return client.sendMessage(new StopCmd(client.getNextMsgId(), getDeviceIndex()));
    }

    /**
     * Send a stop device command.
     *
     * @param inputs  stop inputs
     * @param outputs stop outputs
     * @return future
     */
    public Future<ButtplugMessage> sendStopDeviceCmd(final boolean inputs, final boolean outputs) {
        return client.sendMessage(new StopCmd(client.getNextMsgId(), getDeviceIndex(), inputs, outputs));
    }

    /**
     * Send a stop device command for a specific feature.
     *
     * @param featureIndex feature index
     * @return future
     */
    public Future<ButtplugMessage> sendStopDeviceCmd(final int featureIndex) {
        return client.sendMessage(new StopCmd(client.getNextMsgId(), getDeviceIndex(), featureIndex));
    }

    /**
     * Send a stop device command for a specific feature.
     *
     * @param featureIndex feature index
     * @param inputs       stop inputs
     * @param outputs      stop outputs
     * @return future
     */
    public Future<ButtplugMessage> sendStopDeviceCmd(final int featureIndex,
                                                     final boolean inputs,
                                                     final boolean outputs) {
        return client.sendMessage(new StopCmd(client.getNextMsgId(), getDeviceIndex(), featureIndex, inputs, outputs));
    }

    /**
     * Get the device index.
     *
     * @return device index
     */
    public int getDeviceIndex() {
        return deviceIndex;
    }

    /**
     * Get the device name.
     *
     * @return device name
     */
    public String getName() {
        return deviceName;
    }

    /**
     * Get the device display name.
     *
     * @return device display name
     */
    public String getDisplayName() {
        return deviceDisplayName;
    }

    /**
     * Get the device message timing gap.
     *
     * @return device message timing gap
     */
    public Integer getMessageTimingGap() {
        return deviceMessageTimingGap;
    }

    /**
     * Get the device features.
     *
     * @return device features
     */
    public Map<Integer, ButtplugClientDeviceFeature> getDeviceFeatures() {
        return deviceFeatures;
    }

    /**
     * Check if the device has a specific output type.
     *
     * @param outputType output type
     * @return true if has output
     */
    public boolean hasOutput(final ButtplugOutput outputType) {
        return deviceFeatures.values().stream().anyMatch(f -> f.hasOutput(outputType));
    }

    /**
     * Run an output command.
     *
     * @param featureIndex  feature index
     * @param outputCommand command
     * @return future
     */
    public Future<ButtplugMessage> runOutput(final int featureIndex,
                                             final OutputCmd.IOutputCommand outputCommand) {
        OutputCmd cmd = new OutputCmd(client.getNextMsgId(), deviceIndex, featureIndex);
        cmd.setCommand(outputCommand);
        return client.sendMessage(cmd);
    }

    /**
     * Check if the device has a specific input type.
     *
     * @param inputType input type
     * @return true if has input
     */
    public boolean hasInput(final ButtplugInput inputType) {
        return deviceFeatures.values().stream().anyMatch(f -> f.hasInput(inputType));
    }

    /**
     * Run an input command.
     *
     * @param featureIndex feature index
     * @param inputType    input type
     * @param inputCommand command
     * @return future
     */
    public Future<ButtplugMessage> runInput(final int featureIndex,
                                            final ButtplugInput inputType,
                                            final InputCommandType inputCommand) {
        InputCmd cmd = new InputCmd(client.getNextMsgId(), deviceIndex, featureIndex,
                inputType.getName(), inputCommand);
        return client.sendMessage(cmd);
    }

    /**
     * Read an input.
     *
     * @param featureIndex feature index
     * @param inputType    input type
     * @return future
     */
    public Future<ButtplugMessage> runInputRead(final int featureIndex, final ButtplugInput inputType) {
        return runInput(featureIndex, inputType, InputCommandType.READ);
    }

    /**
     * Subscribe to an input.
     *
     * @param featureIndex feature index
     * @param inputType    input type
     * @return future
     */
    public Future<ButtplugMessage> runInputSubscribe(final int featureIndex, final ButtplugInput inputType) {
        return runInput(featureIndex, inputType, InputCommandType.SUBSCRIBE);
    }

    /**
     * Unsubscribe from an input.
     *
     * @param featureIndex feature index
     * @param inputType    input type
     * @return future
     */
    public Future<ButtplugMessage> runInputUnsubscribe(final int featureIndex, final ButtplugInput inputType) {
        return runInput(featureIndex, inputType, InputCommandType.UNSUBSCRIBE);
    }

    /**
     * Read the battery level.
     *
     * @return battery level
     * @throws ExecutionException   if an error occurs
     * @throws InterruptedException if an error occurs
     * @throws TimeoutException     if an error occurs
     * @throws ButtplugException    if an error occurs
     */
    public int readBattery() throws ExecutionException, InterruptedException,
            TimeoutException, ButtplugException {
        java.util.Optional<ButtplugClientDeviceFeature> feature = deviceFeatures.values().stream()
                .filter(ButtplugClientDeviceFeature::hasBattery).findFirst();
        if (feature.isPresent()) {
            return feature.get().readBattery();
        } else {
            throw new ButtplugDeviceException("Battery feature not found");
        }
    }

    /**
     * Read the RSSI.
     *
     * @return RSSI
     * @throws ExecutionException   if an error occurs
     * @throws InterruptedException if an error occurs
     * @throws TimeoutException     if an error occurs
     * @throws ButtplugException    if an error occurs
     */
    public int readRSSI() throws ExecutionException, InterruptedException,
            TimeoutException, ButtplugException {
        java.util.Optional<ButtplugClientDeviceFeature> feature = deviceFeatures.values().stream()
                .filter(ButtplugClientDeviceFeature::hasRSSI).findFirst();
        if (feature.isPresent()) {
            return feature.get().readRSSI();
        } else {
            throw new ButtplugDeviceException("RSSI feature not found");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ButtplugClientDevice that = (ButtplugClientDevice) o;
        if (deviceIndex != that.deviceIndex
                || (deviceName == null ? that.deviceName != null : !deviceName.equals(that.deviceName))
                || (deviceDisplayName == null ? that.deviceDisplayName != null
                : !deviceDisplayName.equals(that.deviceDisplayName))
                || !Objects.equals(deviceMessageTimingGap, that.deviceMessageTimingGap)) {
            return false;
        }
        if (deviceFeatures.size() != that.deviceFeatures.size()
                || !deviceFeatures.keySet().containsAll(that.deviceFeatures.keySet())) {
            return false;
        }
        for (Integer feat : deviceFeatures.keySet()) {
            if (!deviceFeatures.get(feat).equals(that.deviceFeatures.get(feat))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, deviceIndex, deviceName, deviceDisplayName,
                deviceFeatures, deviceMessageTimingGap);
    }

    /**
     * Run vibrate.
     *
     * @param vibrate vibrate value
     * @return futures
     * @throws ButtplugDeviceFeatureException if an error occurs
     */
    public List<Future<ButtplugMessage>> runVibrateFloat(final float vibrate)
            throws ButtplugDeviceFeatureException {
        List<Future<ButtplugMessage>> msgs = new ArrayList<>();
        for (ButtplugClientDeviceFeature f : deviceFeatures.values()) {
            if (f.hasVibrate()) {
                msgs.add(f.runVibrateFloat(vibrate));
            }
        }
        return msgs;
    }

    /**
     * Run rotate.
     *
     * @param rotate rotate value
     * @return futures
     * @throws ButtplugDeviceFeatureException if an error occurs
     */
    public List<Future<ButtplugMessage>> runRotateFloat(final float rotate)
            throws ButtplugDeviceFeatureException {
        List<Future<ButtplugMessage>> msgs = new ArrayList<>();
        for (ButtplugClientDeviceFeature f : deviceFeatures.values()) {
            if (f.hasRotate()) {
                msgs.add(f.runRotateFloat(rotate));
            }
        }
        return msgs;
    }

    /**
     * Run spray.
     *
     * @param spray spray value
     * @return futures
     * @throws ButtplugDeviceFeatureException if an error occurs
     */
    public List<Future<ButtplugMessage>> runSprayFloat(final float spray)
            throws ButtplugDeviceFeatureException {
        List<Future<ButtplugMessage>> msgs = new ArrayList<>();
        for (ButtplugClientDeviceFeature f : deviceFeatures.values()) {
            if (f.hasSpray()) {
                msgs.add(f.runSprayFloat(spray));
            }
        }
        return msgs;
    }

    /**
     * Run oscillate.
     *
     * @param oscillate oscillate value
     * @return futures
     * @throws ButtplugDeviceFeatureException if an error occurs
     */
    public List<Future<ButtplugMessage>> runOscillateFloat(final float oscillate)
            throws ButtplugDeviceFeatureException {
        List<Future<ButtplugMessage>> msgs = new ArrayList<>();
        for (ButtplugClientDeviceFeature f : deviceFeatures.values()) {
            if (f.hasOscillate()) {
                msgs.add(f.runOscillateFloat(oscillate));
            }
        }
        return msgs;
    }

    /**
     * Run position.
     *
     * @param position position value
     * @return futures
     * @throws ButtplugDeviceFeatureException if an error occurs
     */
    public List<Future<ButtplugMessage>> runPositionFloat(final float position)
            throws ButtplugDeviceFeatureException {
        List<Future<ButtplugMessage>> msgs = new ArrayList<>();
        for (ButtplugClientDeviceFeature f : deviceFeatures.values()) {
            if (f.hasPosition()) {
                msgs.add(f.runPositionFloat(position));
            }
        }
        return msgs;
    }

    /**
     * Run LED.
     *
     * @param led LED value
     * @return futures
     * @throws ButtplugDeviceFeatureException if an error occurs
     */
    public List<Future<ButtplugMessage>> runLedFloat(final float led)
            throws ButtplugDeviceFeatureException {
        List<Future<ButtplugMessage>> msgs = new ArrayList<>();
        for (ButtplugClientDeviceFeature f : deviceFeatures.values()) {
            if (f.hasLed()) {
                msgs.add(f.runLedFloat(led));
            }
        }
        return msgs;
    }

    /**
     * Run temperature.
     *
     * @param temperature temperature value
     * @return futures
     * @throws ButtplugDeviceFeatureException if an error occurs
     */
    public List<Future<ButtplugMessage>> runTemperatureFloat(final float temperature)
            throws ButtplugDeviceFeatureException {
        List<Future<ButtplugMessage>> msgs = new ArrayList<>();
        for (ButtplugClientDeviceFeature f : deviceFeatures.values()) {
            if (f.hasTemperature()) {
                msgs.add(f.runTemperatureFloat(temperature));
            }
        }
        return msgs;
    }

    /**
     * Run constrict.
     *
     * @param constrict constrict value
     * @return futures
     * @throws ButtplugDeviceFeatureException if an error occurs
     */
    public List<Future<ButtplugMessage>> runConstrictFloat(final float constrict)
            throws ButtplugDeviceFeatureException {
        List<Future<ButtplugMessage>> msgs = new ArrayList<>();
        for (ButtplugClientDeviceFeature f : deviceFeatures.values()) {
            if (f.hasConstrict()) {
                msgs.add(f.runConstrictFloat(constrict));
            }
        }
        return msgs;
    }

    /**
     * Run HW position with duration.
     *
     * @param position position value
     * @param duration duration
     * @return futures
     * @throws ButtplugDeviceFeatureException if an error occurs
     */
    public List<Future<ButtplugMessage>> runHwPositionWithDurationFloat(final float position, final int duration)
            throws ButtplugDeviceFeatureException {
        List<Future<ButtplugMessage>> msgs = new ArrayList<>();
        for (ButtplugClientDeviceFeature f : deviceFeatures.values()) {
            if (f.hasHwPositionWithDuration()) {
                msgs.add(f.runHwPositionWithDurationFloat(position, duration));
            }
        }
        return msgs;
    }
}
