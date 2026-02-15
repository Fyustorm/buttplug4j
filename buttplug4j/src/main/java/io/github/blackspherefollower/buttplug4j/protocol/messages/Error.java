package io.github.blackspherefollower.buttplug4j.protocol.messages;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackspherefollower.buttplug4j.ButtplugException;
import io.github.blackspherefollower.buttplug4j.client.ButtplugClientException;
import io.github.blackspherefollower.buttplug4j.client.ButtplugDeviceException;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugConsts;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugMessage;

/**
 * Error message.
 */
public final class Error extends ButtplugMessage {

    /**
     * Error code.
     */
    @JsonProperty(value = "ErrorCode", required = true)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private ErrorClass errorCode;
    /**
     * Error message string.
     */
    @JsonProperty(value = "ErrorMessage", required = true)
    private String errorMessage;
    /**
     * Underlying exception.
     */
    @JsonIgnore
    private ButtplugException exception = null;

    /**
     * Constructor.
     *
     * @param aErrorMessage error message
     * @param aErrorCode    error code
     * @param id            message ID
     */
    public Error(final String aErrorMessage, final ErrorClass aErrorCode, final int id) {
        super(id);
        this.setErrorMessage(aErrorMessage);
        this.setErrorCode(aErrorCode);
    }

    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    private Error() {
        super(ButtplugConsts.DEFAULT_MSG_ID);
        this.setErrorMessage("");
        this.setErrorCode(ErrorClass.ERROR_UNKNOWN);
    }

    /**
     * Constructor from exception.
     *
     * @param e exception
     */
    public Error(final ButtplugException e) {
        super(ButtplugConsts.SYSTEM_MSG_ID);
        this.setErrorMessage(e.getMessage());
        this.setErrorCode(ErrorClass.ERROR_UNKNOWN);
        this.exception = e;
    }

    /**
     * Constructor from exception and ID.
     *
     * @param e  exception
     * @param id message ID
     */
    public Error(final ButtplugException e, final int id) {
        super(id);
        this.setErrorMessage(e.getMessage());
        this.setErrorCode(ErrorClass.ERROR_UNKNOWN);
        this.exception = e;
    }

    /**
     * Get error code.
     *
     * @return error code
     */
    public ErrorClass getErrorCode() {
        return errorCode;
    }

    /**
     * Set error code.
     *
     * @param aErrorCode error code
     */
    public void setErrorCode(final ErrorClass aErrorCode) {
        this.errorCode = aErrorCode;
    }

    /**
     * Get error message.
     *
     * @return error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set error message.
     *
     * @param aErrorMessage error message
     */
    public void setErrorMessage(final String aErrorMessage) {
        this.errorMessage = aErrorMessage;
    }

    /**
     * Get underlying exception.
     *
     * @return exception
     */
    public ButtplugException getException() {
        if (exception != null) {
            return exception;
        } else if (errorCode == ErrorClass.ERROR_DEVICE) {
            return new ButtplugDeviceException(errorMessage);
        } else if (errorCode != null) {
            return new ButtplugClientException(errorMessage);
        }
        return null;
    }

    /**
     * Error class enum.
     */
    public enum ErrorClass {
        /**
         * Unknown.
         */
        ERROR_UNKNOWN,
        /**
         * Init.
         */
        ERROR_INIT,
        /**
         * Ping.
         */
        ERROR_PING,
        /**
         * Message.
         */
        ERROR_MSG,
        /**
         * Device.
         */
        ERROR_DEVICE,
    }
}
