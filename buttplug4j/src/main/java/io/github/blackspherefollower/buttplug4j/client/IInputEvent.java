package io.github.blackspherefollower.buttplug4j.client;

import io.github.blackspherefollower.buttplug4j.protocol.messages.InputReading;

/**
 * IInputEvent interface.
 */
public interface IInputEvent {
    /**
     * Called when input event is received.
     *
     * @param msg reading
     */
    void inputEvent(InputReading msg);
}
