package io.github.blackspherefollower.buttplug4j;

/**
 * ButtplugException.
 */
public class ButtplugException extends Exception {

    /**
     * Error message.
     */
    private String errorMessage = "";

    /**
     * Constructor.
     *
     * @param exception cause
     */
    public ButtplugException(final Exception exception) {
        super(exception);
    }

    /**
     * Constructor.
     */
    public ButtplugException() {
    }

    @Override
    public final String getMessage() {
        return errorMessage;
    }

    /**
     * Set error message.
     *
     * @param aErrorMessage error message
     */
    protected final void setMessage(final String aErrorMessage) {
        this.errorMessage = aErrorMessage;
    }
}
