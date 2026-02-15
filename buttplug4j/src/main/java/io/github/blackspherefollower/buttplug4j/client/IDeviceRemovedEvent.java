package io.github.blackspherefollower.buttplug4j.client;

/**
 * IDeviceRemovedEvent interface.
 */
public interface IDeviceRemovedEvent {
    /**
     * Called when a device is removed.
     *
     * @param index device index
     */
    void deviceRemoved(int index);
}
