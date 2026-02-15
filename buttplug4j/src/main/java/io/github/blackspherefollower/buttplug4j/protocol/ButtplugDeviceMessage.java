package io.github.blackspherefollower.buttplug4j.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ButtplugDeviceMessage.
 */
public abstract class ButtplugDeviceMessage extends ButtplugMessage {

    /**
     * Device index.
     */
    @JsonProperty(value = "DeviceIndex", required = true)
    private long deviceIndex;

    /**
     * Constructor.
     *
     * @param id           message ID
     * @param aDeviceIndex device index
     */
    public ButtplugDeviceMessage(final int id, final long aDeviceIndex) {
        super(id);
        this.setDeviceIndex(aDeviceIndex);
    }

    /**
     * Get device index.
     *
     * @return index
     */
    public final long getDeviceIndex() {
        return deviceIndex;
    }

    /**
     * Set device index.
     *
     * @param aDeviceIndex index
     */
    public final void setDeviceIndex(final long aDeviceIndex) {
        this.deviceIndex = aDeviceIndex;
    }
}
