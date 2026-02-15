package io.github.blackspherefollower.buttplug4j.client;

import io.github.blackspherefollower.buttplug4j.ButtplugException;

/**
 * ButtplugDeviceFeatureException.
 */
public class ButtplugDeviceFeatureException extends ButtplugException {
    /**
     * Buttplug Device Feature does not support this Output Type.
     *
     * @param cmd output type
     */
    public ButtplugDeviceFeatureException(final ButtplugOutput cmd) {
        super();
        setMessage("Buttplug Device Feature does not support " + cmd.getName());
    }

    /**
     * Buttplug Device Feature does not support this Input Type.
     *
     * @param cmd input type
     */
    public ButtplugDeviceFeatureException(final ButtplugInput cmd) {
        super();
        setMessage("Buttplug Device Feature does not support " + cmd.getName());
    }

    /**
     * Buttplug Device Feature does not handle this step value.
     *
     * @param value value
     * @param steps max steps
     */
    public ButtplugDeviceFeatureException(final int value, final int steps) {
        super();
        setMessage("Buttplug Device Feature value out of range");
    }
}
