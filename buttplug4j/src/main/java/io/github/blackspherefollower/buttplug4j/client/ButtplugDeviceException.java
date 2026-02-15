package io.github.blackspherefollower.buttplug4j.client;

import io.github.blackspherefollower.buttplug4j.ButtplugException;

/**
 * ButtplugDeviceException.
 */
public class ButtplugDeviceException extends ButtplugException {
    /**
     * Constructor.
     *
     * @param errorMessage error message
     */
    public ButtplugDeviceException(final String errorMessage) {
        setMessage(errorMessage);
    }
}
