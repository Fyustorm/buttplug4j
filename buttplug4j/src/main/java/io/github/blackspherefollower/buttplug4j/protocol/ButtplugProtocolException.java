package io.github.blackspherefollower.buttplug4j.protocol;

import io.github.blackspherefollower.buttplug4j.ButtplugException;

/**
 * ButtplugProtocolException.
 */
public class ButtplugProtocolException extends ButtplugException {
    /**
     * Constructor.
     *
     * @param e cause
     */
    public ButtplugProtocolException(final Exception e) {
        setMessage("Buttplug JSON message exception");
        this.initCause(e);
    }
}
