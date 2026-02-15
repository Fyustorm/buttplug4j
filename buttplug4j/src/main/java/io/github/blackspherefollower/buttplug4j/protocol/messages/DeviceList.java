package io.github.blackspherefollower.buttplug4j.protocol.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugConsts;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugMessage;

import java.util.HashMap;

/**
 * DeviceList message.
 */
public final class DeviceList extends ButtplugMessage {

    /**
     * Devices.
     */
    @JsonProperty(value = "Devices", required = true)
    private HashMap<Integer, Device> devices;

    /**
     * Constructor.
     *
     * @param aDevices devices
     * @param id       message ID
     */
    public DeviceList(final HashMap<Integer, Device> aDevices, final int id) {
        super(id);
        this.devices = aDevices;
    }

    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    private DeviceList() {
        super(ButtplugConsts.DEFAULT_MSG_ID);
        this.setDevices(new HashMap<>());
    }

    /**
     * Get devices.
     *
     * @return devices
     */
    public HashMap<Integer, Device> getDevices() {
        return devices;
    }

    /**
     * Set devices.
     *
     * @param aDevices devices
     */
    public void setDevices(final HashMap<Integer, Device> aDevices) {
        this.devices = aDevices;
    }
}
