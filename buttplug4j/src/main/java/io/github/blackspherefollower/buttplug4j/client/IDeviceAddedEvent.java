package io.github.blackspherefollower.buttplug4j.client;

/**
 * IDeviceAddedEvent interface.
 */
public interface IDeviceAddedEvent {
    /**
     * Called when a device is added.
     *
     * @param dev device
     */
    void deviceAdded(ButtplugClientDevice dev);
}
