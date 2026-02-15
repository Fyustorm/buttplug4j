package io.github.blackspherefollower.buttplug4j.client;

import io.github.blackspherefollower.buttplug4j.protocol.messages.Error;

/**
 * IErrorEvent interface.
 */
public interface IErrorEvent {
    /**
     * Called when an error is received.
     *
     * @param err error
     */
    void errorReceived(Error err);
}
