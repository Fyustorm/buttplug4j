package io.github.blackspherefollower.buttplug4j.protocol.messages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

/**
 * Device.
 */
public class Device {

    /**
     * Device index.
     */
    @JsonProperty(value = "DeviceIndex", required = true)
    private int deviceIndex;

    /**
     * Device name.
     */
    @JsonProperty(value = "DeviceName", required = true)
    private String deviceName;

    /**
     * Device message timing gap.
     */
    @JsonProperty(value = "DeviceMessageTimingGap")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer deviceMessageTimingGap = null;

    /**
     * Device display name.
     */
    @JsonProperty(value = "DeviceDisplayName")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String deviceDisplayName;

    /**
     * Device features.
     */
    @JsonProperty(value = "DeviceFeatures", required = true)
    private HashMap<Integer, DeviceFeature> deviceFeatures;

    /**
     * Constructor.
     *
     * @param deviceIndex            device index
     * @param deviceName             device name
     * @param deviceFeatures         features
     * @param deviceMessageTimingGap timing gap
     * @param deviceDisplayName      display name
     */
    public Device(final int deviceIndex, final String deviceName,
                  final HashMap<Integer, DeviceFeature> deviceFeatures, final int deviceMessageTimingGap,
                  final String deviceDisplayName) {
        this.deviceName = deviceName;
        this.deviceIndex = deviceIndex;
        this.deviceFeatures = deviceFeatures;
        this.deviceMessageTimingGap = deviceMessageTimingGap;
        this.deviceDisplayName = deviceDisplayName;
    }

    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    private Device() {
        this.deviceName = "";
        this.deviceIndex = -1;
        this.deviceFeatures = new HashMap<>();
        this.deviceMessageTimingGap = null;
        this.deviceDisplayName = "";
    }

    /**
     * Get device index.
     *
     * @return index
     */
    public int getDeviceIndex() {
        return deviceIndex;
    }

    /**
     * Set device index.
     *
     * @param aDeviceIndex index
     */
    public void setDeviceIndex(final int aDeviceIndex) {
        this.deviceIndex = aDeviceIndex;
    }

    /**
     * Get device name.
     *
     * @return name
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Set device name.
     *
     * @param aDeviceName name
     */
    public void setDeviceName(final String aDeviceName) {
        this.deviceName = aDeviceName;
    }

    /**
     * Get device message timing gap.
     *
     * @return gap
     */
    public Integer getDeviceMessageTimingGap() {
        return deviceMessageTimingGap;
    }

    /**
     * Set device message timing gap.
     *
     * @param aDeviceMessageTimingGap gap
     */
    public void setDeviceMessageTimingGap(final Integer aDeviceMessageTimingGap) {
        this.deviceMessageTimingGap = aDeviceMessageTimingGap;
    }

    /**
     * Get device display name.
     *
     * @return display name
     */
    public String getDeviceDisplayName() {
        return deviceDisplayName;
    }

    /**
     * Set device display name.
     *
     * @param aDeviceDisplayName display name
     */
    public void setDeviceDisplayName(final String aDeviceDisplayName) {
        this.deviceDisplayName = aDeviceDisplayName;
    }

    /**
     * Get device features.
     *
     * @return features
     */
    public HashMap<Integer, DeviceFeature> getDeviceFeatures() {
        return deviceFeatures;
    }

    /**
     * Set device features.
     *
     * @param aDeviceFeatures features
     */
    public void setDeviceFeatures(final HashMap<Integer, DeviceFeature> aDeviceFeatures) {
        this.deviceFeatures = aDeviceFeatures;
    }
}
