package io.github.blackspherefollower.buttplug4j.client;

import io.github.blackspherefollower.buttplug4j.ButtplugException;

public class ButtplugDeviceFeatureException extends ButtplugException {
    public ButtplugDeviceFeatureException(final ButtplugOutput cmd) {
        super();
        setMessage("Buttplug Device Feature does not support " + cmd.getName());
    }

    public ButtplugDeviceFeatureException(final ButtplugInput cmd) {
        super();
        setMessage("Buttplug Device Feature does not support " + cmd.getName());
    }

    public ButtplugDeviceFeatureException(final int value, final int steps) {
        super();
        setMessage("Buttplug Device Feature value out of range");
    }
}
