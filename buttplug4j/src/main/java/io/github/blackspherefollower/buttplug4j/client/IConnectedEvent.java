package io.github.blackspherefollower.buttplug4j.client;

/**
 * IConnectedEvent interface.
 */
public interface IConnectedEvent {
    /**
     * Called when the client has connected.
     *
     * @param client client
     */
    void onConnected(ButtplugClient client);
}
