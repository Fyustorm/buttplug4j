package io.github.blackspherefollower.buttplug4j.client;

import io.github.blackspherefollower.buttplug4j.ButtplugException;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugMessage;
import io.github.blackspherefollower.buttplug4j.protocol.messages.DeviceFeature;
import io.github.blackspherefollower.buttplug4j.protocol.messages.Error;
import io.github.blackspherefollower.buttplug4j.protocol.messages.InputReading;
import io.github.blackspherefollower.buttplug4j.protocol.messages.OutputCmd;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ButtplugClientDeviceFeature {

    private final ButtplugClientDevice device;
    private final String description;

    public final HashMap<ButtplugOutput, DeviceFeature.OutputDescriptor> getOutput() {
        return output;
    }

    public final HashMap<ButtplugInput, DeviceFeature.InputDescriptor> getInput() {
        return input;
    }

    private final HashMap<ButtplugOutput, DeviceFeature.OutputDescriptor> output;
    private final HashMap<ButtplugInput, DeviceFeature.InputDescriptor> input;

    public final int getFeatureIndex() {
        return featureIndex;
    }

    private final int featureIndex;

    public ButtplugClientDeviceFeature(final ButtplugClientDevice device, final DeviceFeature feature) {
        this.device = device;
        this.featureIndex = feature.getFeatureIndex();
        this.description = feature.getFeatureDescription();
        this.output = new HashMap<>();
        if (feature.getOutput() != null) {
            feature.getOutput().forEach(outputDescriptor -> {
                try {
                    this.output.put(ButtplugOutput.fromString(outputDescriptor.getClass().getSimpleName()), outputDescriptor);
                } catch (IllegalArgumentException e) {
                    // not a supported output type
                }
            });
        }
        this.input = new HashMap<>();
        if (feature.getInput() != null) {
                feature.getInput().forEach(inputDescriptor -> {try {
                    this.input.put(ButtplugInput.fromString(inputDescriptor.getClass().getSimpleName()), inputDescriptor);
                } catch (IllegalArgumentException e) {
                    // not a supported output type
                }
            });
        }
    }

    private int getStepFromFloat(final ButtplugOutput type, final float value) throws ButtplugDeviceFeatureException {
        DeviceFeature.OutputDescriptor desc = output.get(type);
        if (desc == null) {
            throw new ButtplugDeviceFeatureException(type);
        }
        if (desc instanceof DeviceFeature.SteppedOutputDescriptor) {
            double steps = ((DeviceFeature.SteppedOutputDescriptor) desc).getValue()[1];
            steps *= value;
            return (int) Math.floor(steps);
        } else {
            throw new ButtplugDeviceFeatureException(type);
        }
    }

    private void checkStepRange(final ButtplugOutput type, final int value) throws ButtplugDeviceFeatureException {
        DeviceFeature.OutputDescriptor desc = output.get(type);
        if (desc == null) {
            throw new ButtplugDeviceFeatureException(type);
        }
        if (desc instanceof DeviceFeature.SteppedOutputDescriptor) {
            int steps = ((DeviceFeature.SteppedOutputDescriptor) desc).getValue()[1];
            if (value > steps || value < 0) {
                throw new ButtplugDeviceFeatureException(value, steps);
            }
        } else {
            throw new ButtplugDeviceFeatureException(type);
        }
    }

    public boolean hasOutput(final ButtplugOutput type) {
        return output.get(type) != null;
    }

    public boolean hasVibrate() {
        return output.get(ButtplugOutput.VIBRATE) != null;
    }

    public Future<ButtplugMessage> runVibrate(final int vibrate) throws ButtplugDeviceFeatureException {
        checkStepRange(ButtplugOutput.VIBRATE, vibrate);
        return device.runOutput(featureIndex, new OutputCmd.Vibrate(vibrate));
    }

    public Future<ButtplugMessage> runVibrateFloat(final float vibrate) throws ButtplugDeviceFeatureException {
        int steps = getStepFromFloat(ButtplugOutput.VIBRATE, vibrate);
        checkStepRange(ButtplugOutput.VIBRATE, steps);
        return device.runOutput(featureIndex, new OutputCmd.Vibrate(steps));
    }

    public boolean hasRotate() {
        return output.get(ButtplugOutput.ROTATE) != null;
    }

    public Future<ButtplugMessage> runRotate(final int rotate) throws ButtplugDeviceFeatureException {
        checkStepRange(ButtplugOutput.ROTATE, rotate);
        return device.runOutput(featureIndex, new OutputCmd.Rotate(rotate));
    }

    public Future<ButtplugMessage> runRotateFloat(final float rotate) throws ButtplugDeviceFeatureException {
        int steps = getStepFromFloat(ButtplugOutput.ROTATE, rotate);
        checkStepRange(ButtplugOutput.ROTATE, steps);
        return device.runOutput(featureIndex, new OutputCmd.Rotate(steps));
    }

    public boolean hasConstrict() {
        return output.get(ButtplugOutput.CONSTRICT) != null;
    }

    public Future<ButtplugMessage> runConstrict(final int constrict) throws ButtplugDeviceFeatureException {
        checkStepRange(ButtplugOutput.CONSTRICT, constrict);
        return device.runOutput(featureIndex, new OutputCmd.Constrict(constrict));
    }

    public Future<ButtplugMessage> runConstrictFloat(final float constrict) throws ButtplugDeviceFeatureException {
        int steps = getStepFromFloat(ButtplugOutput.CONSTRICT, constrict);
        checkStepRange(ButtplugOutput.CONSTRICT, steps);
        return device.runOutput(featureIndex, new OutputCmd.Constrict(steps));
    }

    public boolean hasSpray() {
        return output.get(ButtplugOutput.SPRAY) != null;
    }

    public Future<ButtplugMessage> runSpray(final int spray) throws ButtplugDeviceFeatureException {
        checkStepRange(ButtplugOutput.SPRAY, spray);
        return device.runOutput(featureIndex, new OutputCmd.Spray(spray));
    }

    public Future<ButtplugMessage> runSprayFloat(final float spray) throws ButtplugDeviceFeatureException {
        int steps = getStepFromFloat(ButtplugOutput.SPRAY, spray);
        checkStepRange(ButtplugOutput.SPRAY, steps);
        return device.runOutput(featureIndex, new OutputCmd.Spray(steps));
    }

    public boolean hasPosition() {
        return output.get(ButtplugOutput.POSITION) != null;
    }

    public Future<ButtplugMessage> runPosition(final int position) throws ButtplugDeviceFeatureException {
        checkStepRange(ButtplugOutput.POSITION, position);
        return device.runOutput(featureIndex, new OutputCmd.Position(position));
    }

    public Future<ButtplugMessage> runPositionFloat(final float position) throws ButtplugDeviceFeatureException {
        int steps = getStepFromFloat(ButtplugOutput.POSITION, position);
        checkStepRange(ButtplugOutput.POSITION, steps);
        return device.runOutput(featureIndex, new OutputCmd.Position(steps));
    }

    public boolean hasHwPositionWithDuration() {
        return output.get(ButtplugOutput.HW_POSITION_WITH_DURATION) != null;
    }

    public Future<ButtplugMessage> runHwPositionWithDuration(final int position, final int duration) throws ButtplugDeviceFeatureException {
        checkStepRange(ButtplugOutput.HW_POSITION_WITH_DURATION, position);
        return device.runOutput(featureIndex, new OutputCmd.HwPositionWithDuration(position, duration));
    }

    public Future<ButtplugMessage> runHwPositionWithDurationFloat(final float position, final int duration) throws ButtplugDeviceFeatureException {
        int steps = getStepFromFloat(ButtplugOutput.HW_POSITION_WITH_DURATION, position);
        checkStepRange(ButtplugOutput.HW_POSITION_WITH_DURATION, steps);
        return device.runOutput(featureIndex, new OutputCmd.HwPositionWithDuration(steps, duration));
    }

    public boolean hasLed() {
        return output.get(ButtplugOutput.LED) != null;
    }

    public Future<ButtplugMessage> runLed(final int led) throws ButtplugDeviceFeatureException {
        checkStepRange(ButtplugOutput.LED, led);
        return device.runOutput(featureIndex, new OutputCmd.Led(led));
    }

    public Future<ButtplugMessage> runLedFloat(final float led) throws ButtplugDeviceFeatureException {
        int steps = getStepFromFloat(ButtplugOutput.LED, led);
        checkStepRange(ButtplugOutput.LED, steps);
        return device.runOutput(featureIndex, new OutputCmd.Led(steps));
    }

    public boolean hasOscillate() {
        return output.get(ButtplugOutput.OSCILLATE) != null;
    }

    public Future<ButtplugMessage> runOscillate(final int oscillate) throws ButtplugDeviceFeatureException {
        checkStepRange(ButtplugOutput.OSCILLATE, oscillate);
        return device.runOutput(featureIndex, new OutputCmd.Oscillate(oscillate));
    }

    public Future<ButtplugMessage> runOscillateFloat(final float oscillate) throws ButtplugDeviceFeatureException {
        int steps = getStepFromFloat(ButtplugOutput.OSCILLATE, oscillate);
        checkStepRange(ButtplugOutput.OSCILLATE, steps);
        return device.runOutput(featureIndex, new OutputCmd.Oscillate(steps));
    }

    public boolean hasTemperature() {
        return output.get(ButtplugOutput.TEMPERATURE) != null;
    }

    public Future<ButtplugMessage> runTemperature(final int temperature) throws ButtplugDeviceFeatureException {
        checkStepRange(ButtplugOutput.TEMPERATURE, temperature);
        return device.runOutput(featureIndex, new OutputCmd.Temperature(temperature));
    }

    public Future<ButtplugMessage> runTemperatureFloat(final float temperature) throws ButtplugDeviceFeatureException {
        int steps = getStepFromFloat(ButtplugOutput.TEMPERATURE, temperature);
        checkStepRange(ButtplugOutput.TEMPERATURE, steps);
        return device.runOutput(featureIndex, new OutputCmd.Temperature(steps));
    }

    private void checkInput(final ButtplugInput type) throws ButtplugDeviceFeatureException {
        if (input.get(type) == null) {
            throw new ButtplugDeviceFeatureException(type);
        }
    }

    public boolean hasBattery() {
        return input.get(ButtplugInput.BATTERY) != null;
    }

    public int readBattery() throws ButtplugException, ExecutionException, InterruptedException, TimeoutException {
        checkInput(ButtplugInput.BATTERY);
        ButtplugMessage msg = device.runInputRead(featureIndex, ButtplugInput.BATTERY).get(2, TimeUnit.SECONDS);
        return extractIntegerReading(msg);
    }

    private int extractIntegerReading(ButtplugMessage msg) throws ButtplugException {
        if( msg instanceof InputReading && ((InputReading) msg).getData() instanceof InputReading.InputIntegerData) {
            return ((InputReading.InputIntegerData)((InputReading) msg).getData()).getValue();
        } else if (msg instanceof Error) {
            throw ((Error) msg).getException();
        } else {
            throw new ButtplugDeviceException("Unexpected message type: " + msg.getClass().getName());
        }
    }

    public boolean hasRSSI() {
        return input.get(ButtplugInput.RSSI) != null;
    }

    public int readRSSI() throws ButtplugException, ExecutionException, InterruptedException, TimeoutException {
        checkInput(ButtplugInput.RSSI);
        ButtplugMessage msg = device.runInputRead(featureIndex, ButtplugInput.RSSI).get(2, TimeUnit.SECONDS);
        return extractIntegerReading(msg);
    }

    public String getDescription() {
        return description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ButtplugClientDeviceFeature that = (ButtplugClientDeviceFeature) o;
        if (featureIndex != that.featureIndex ||
                !description.equals(that.description) ||
                (output == null) != (that.output == null) ||
                (input == null) != (that.input == null)) {
            return false;
        }
        if (output != null) {
            if (output.size() != that.output.size() || !output.keySet().equals(that.output.keySet())) {
                return false;
            }
            for (ButtplugOutput type : output.keySet()) {
                if (!output.get(type).equals(that.output.get(type))) {
                    return false;
                }
            }
        }
        if (input != null) {
            if (input.size() != that.input.size() || !input.keySet().equals(that.input.keySet())) {
                return false;
            }
            for (ButtplugInput type : input.keySet()) {
                if (!input.get(type).equals(that.input.get(type))) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean hasInput(ButtplugInput inputType) {
        return input.containsKey(inputType);
    }
}
