package io.github.blackspherefollower.buttplug4j.client;

/**
 * IDeviceChangedEvent interface.
 */
public interface IDeviceChangedEvent {
    /**
     * Called when a device is changed.
     *
     * @param dev device
     */
    void deviceChanged(ButtplugClientDevice dev);
}
