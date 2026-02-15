package io.github.blackspherefollower.buttplug4j.client;

import io.github.blackspherefollower.buttplug4j.ButtplugException;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugMessage;
import io.github.blackspherefollower.buttplug4j.protocol.messages.*;
import io.github.blackspherefollower.buttplug4j.protocol.messages.Error;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class ButtplugClientDevice {

    private final ButtplugClient client;
    private final int deviceIndex;
    private final String deviceName;
    private final String deviceDisplayName;
    private final HashMap<Integer, ButtplugClientDeviceFeature> deviceFeatures = new HashMap<>();
    private Integer deviceMessageTimingGap = null;

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

    public final Future<ButtplugMessage> sendStopDeviceCmd() {
        return client.sendMessage(new StopCmd(client.getNextMsgId(), getDeviceIndex()));
    }

    public final Future<ButtplugMessage> sendStopDeviceCmd(final boolean inputs, final boolean outputs) {
        return client.sendMessage(new StopCmd(client.getNextMsgId(), getDeviceIndex(), inputs, outputs));
    }

    public final Future<ButtplugMessage> sendStopDeviceCmd(final int featureIndex) {
        return client.sendMessage(new StopCmd(client.getNextMsgId(), getDeviceIndex(), featureIndex));
    }

    public final Future<ButtplugMessage> sendStopDeviceCmd(final int featureIndex, final boolean inputs, final boolean outputs) {
        return client.sendMessage(new StopCmd(client.getNextMsgId(), getDeviceIndex(), featureIndex, inputs, outputs));
    }

    public final int getDeviceIndex() {
        return deviceIndex;
    }

    public final String getName() {
        return deviceName;
    }

    public final String getDisplayName() {
        return deviceDisplayName;
    }

    public final Integer getMessageTimingGap() {
        return deviceMessageTimingGap;
    }

    public final Map<Integer, ButtplugClientDeviceFeature> getDeviceFeatures() {
        return deviceFeatures;
    }

    public final boolean hasOutput(ButtplugOutput outputType) {
        return deviceFeatures.values().stream().anyMatch(f -> f.hasOutput(outputType));
    }

    public Future<ButtplugMessage> runOutput(int featureIndex, OutputCmd.IOutputCommand outputCommand) {
        OutputCmd cmd = new OutputCmd(client.getNextMsgId(), deviceIndex, featureIndex);
        cmd.setCommand(outputCommand);
        return client.sendMessage(cmd);
    }

    public final boolean hasInput(ButtplugInput inputType) {
        return deviceFeatures.values().stream().anyMatch(f -> f.hasInput(inputType));
    }

    public Future<ButtplugMessage> runInput(int featureIndex, final ButtplugInput inputType, final InputCommandType inputCommand) {
        InputCmd cmd = new InputCmd(client.getNextMsgId(), deviceIndex, featureIndex, inputType.getName(), inputCommand);
        return client.sendMessage(cmd);
    }

    public Future<ButtplugMessage> runInputRead(int featureIndex, final ButtplugInput inputType) {
        return runInput(featureIndex, inputType, InputCommandType.READ);
    }

    public Future<ButtplugMessage> runInputSubscribe(int featureIndex, final ButtplugInput inputType) {
        return runInput(featureIndex, inputType, InputCommandType.SUBSCRIBE);
    }

    public Future<ButtplugMessage> runInputUnsubscribe(int featureIndex, final ButtplugInput inputType) {
        return runInput(featureIndex, inputType, InputCommandType.UNSUBSCRIBE);
    }

    public int readBattery() throws ExecutionException, InterruptedException, TimeoutException, ButtplugException {
        java.util.Optional<ButtplugClientDeviceFeature> feature = deviceFeatures.values().stream().filter(f -> f.getInput().containsKey("Battery")).findFirst();
        if( feature.isPresent() ) {
            return feature.get().readBattery();
        } else {
            throw new ButtplugDeviceException("Battery feature not found");
        }
    }

    public int readRSSI() throws ExecutionException, InterruptedException, TimeoutException, ButtplugException {
        java.util.Optional<ButtplugClientDeviceFeature> feature = deviceFeatures.values().stream().filter(f -> f.getInput().containsKey("Battery")).findFirst();
        if( feature.isPresent() ) {
            return feature.get().readRSSI();
        } else {
            throw new ButtplugDeviceException("RSSI feature not found");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ButtplugClientDevice that = (ButtplugClientDevice) o;
        if (deviceIndex != that.deviceIndex || !deviceName.equals(that.deviceName) || !deviceDisplayName.equals(that.deviceDisplayName) || !Objects.equals(deviceMessageTimingGap, that.deviceMessageTimingGap)) {
            return false;
        }
        if (deviceFeatures.size() != that.deviceFeatures.size() ||
                !deviceFeatures.keySet().containsAll(that.deviceFeatures.keySet())) {
            return false;
        }
        for (Integer feat : deviceFeatures.keySet()) {
            if (!deviceFeatures.get(feat).equals(that.deviceFeatures.get(feat))) {
                return false;
            }
        }
        return true;
    }

    public List<Future<ButtplugMessage>> runVibrateFloat(final float vibrate) throws ButtplugDeviceFeatureException {
        List<Future<ButtplugMessage>> msgs = new ArrayList<>();
        for( ButtplugClientDeviceFeature f : deviceFeatures.values() ) {
            if( f.hasVibrate() ) {
                msgs.add(f.runVibrateFloat(vibrate));
            }
        }
        return msgs;
    }

    public List<Future<ButtplugMessage>> runRotateFloat(final float rotate) throws ButtplugDeviceFeatureException {
        List<Future<ButtplugMessage>> msgs = new ArrayList<>();
        for( ButtplugClientDeviceFeature f : deviceFeatures.values() ) {
            if( f.hasRotate() ) {
                msgs.add(f.runRotateFloat(rotate));
            }
        }
        return msgs;
    }

    public List<Future<ButtplugMessage>> runSprayFloat(final float spray) throws ButtplugDeviceFeatureException {
        List<Future<ButtplugMessage>> msgs = new ArrayList<>();
        for( ButtplugClientDeviceFeature f : deviceFeatures.values() ) {
            if( f.hasSpray() ) {
                msgs.add(f.runSprayFloat(spray));
            }
        }
        return msgs;
    }

    public List<Future<ButtplugMessage>> runOscillateFloat(final float oscillate) throws ButtplugDeviceFeatureException {
        List<Future<ButtplugMessage>> msgs = new ArrayList<>();
        for( ButtplugClientDeviceFeature f : deviceFeatures.values() ) {
            if( f.hasOscillate() ) {
                msgs.add(f.runOscillateFloat(oscillate));
            }
        }
        return msgs;
    }

    public List<Future<ButtplugMessage>> runPositionFloat(final float position) throws ButtplugDeviceFeatureException {
        List<Future<ButtplugMessage>> msgs = new ArrayList<>();
        for( ButtplugClientDeviceFeature f : deviceFeatures.values() ) {
            if( f.hasPosition() ) {
                msgs.add(f.runPositionFloat(position));
            }
        }
        return msgs;
    }

    public List<Future<ButtplugMessage>> runLedFloat(final float led) throws ButtplugDeviceFeatureException {
        List<Future<ButtplugMessage>> msgs = new ArrayList<>();
        for( ButtplugClientDeviceFeature f : deviceFeatures.values() ) {
            if( f.hasLed() ) {
                msgs.add(f.runLedFloat(led));
            }
        }
        return msgs;
    }

    public List<Future<ButtplugMessage>> runTemperatureFloat(final float temperature) throws ButtplugDeviceFeatureException {
        List<Future<ButtplugMessage>> msgs = new ArrayList<>();
        for( ButtplugClientDeviceFeature f : deviceFeatures.values() ) {
            if( f.hasTemperature() ) {
                msgs.add(f.runTemperatureFloat(temperature));
            }
        }
        return msgs;
    }

    public List<Future<ButtplugMessage>> runConstrictFloat(final float constrict) throws ButtplugDeviceFeatureException {
        List<Future<ButtplugMessage>> msgs = new ArrayList<>();
        for( ButtplugClientDeviceFeature f : deviceFeatures.values() ) {
            if( f.hasConstrict() ) {
                msgs.add(f.runConstrictFloat(constrict));
            }
        }
        return msgs;
    }

    public List<Future<ButtplugMessage>> runHwPositionWithDurationFloat(final float position, final int duration) throws ButtplugDeviceFeatureException {
        List<Future<ButtplugMessage>> msgs = new ArrayList<>();
        for( ButtplugClientDeviceFeature f : deviceFeatures.values() ) {
            if( f.hasHwPositionWithDuration() ) {
                msgs.add(f.runHwPositionWithDurationFloat(position, duration));
            }
        }
        return msgs;
    }
}
