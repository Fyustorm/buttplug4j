package io.github.blackspherefollower.buttplug4j;

public class ButtplugException extends Exception {

    private String errorMessage = "";

    public ButtplugException(Exception exception) {
        super(exception);
    }
    public ButtplugException() {
    }

    @Override
    public final String getMessage() {
        return errorMessage;
    }

    protected final void setMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
