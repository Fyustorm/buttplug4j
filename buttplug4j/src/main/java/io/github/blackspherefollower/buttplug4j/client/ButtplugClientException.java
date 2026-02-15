package io.github.blackspherefollower.buttplug4j.client;

import io.github.blackspherefollower.buttplug4j.ButtplugException;

/**
 * ButtplugClientException.
 */
public class ButtplugClientException extends ButtplugException {
    /**
     * Constructor.
     *
     * @param errorMessage error message
     */
    public ButtplugClientException(final String errorMessage) {
        setMessage(errorMessage);
    }

    /**
     * Constructor.
     *
     * @param exception cause
     */
    public ButtplugClientException(final Exception exception) {
        super(exception);
        setMessage(exception.getMessage());
    }
}
