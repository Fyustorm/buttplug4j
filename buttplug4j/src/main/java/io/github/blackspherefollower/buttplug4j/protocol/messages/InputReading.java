package io.github.blackspherefollower.buttplug4j.protocol.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugDeviceMessage;

/**
 * InputReading message.
 */
public class InputReading extends ButtplugDeviceMessage {

    /**
     * Feature index.
     */
    @JsonProperty(value = "FeatureIndex", required = true)
    private int featureIndex;
    /**
     * Input data.
     */
    @JsonProperty(value = "Reading", required = true)
    private InputData data;

    /**
     * Constructor.
     *
     * @param id            message ID
     * @param deviceIndex   device index
     * @param aFeatureIndex feature index
     */
    public InputReading(final int id, final long deviceIndex, final int aFeatureIndex) {
        super(id, deviceIndex);
        this.featureIndex = aFeatureIndex;
    }

    /**
     * Constructor.
     */
    public InputReading() {
        super(-1, -1);
        this.featureIndex = -1;
    }

    /**
     * Get data.
     *
     * @return data
     */
    public final InputData getData() {
        return data;
    }

    /**
     * Set data.
     *
     * @param aData data
     */
    public final void setData(final InputData aData) {
        this.data = aData;
    }

    /**
     * Get feature index.
     *
     * @return index
     */
    public final int getFeatureIndex() {
        return featureIndex;
    }

    /**
     * Set feature index.
     *
     * @param aFeatureIndex index
     */
    public final void setFeatureIndex(final int aFeatureIndex) {
        this.featureIndex = aFeatureIndex;
    }

    /**
     * Input data wrapper.
     */
    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = BatteryData.class, name = "Battery"),
            @JsonSubTypes.Type(value = RssiData.class, name = "RSSI"),
            @JsonSubTypes.Type(value = ButtonData.class, name = "Button"),
            @JsonSubTypes.Type(value = PresureData.class, name = "Pressure"),
            @JsonSubTypes.Type(value = Position.class, name = "Position"),
    })
    public static class InputData {
    }

    /**
     * Input integer data.
     */
    public static class InputIntegerData extends InputData {
        /**
         * Value.
         */
        @JsonProperty(value = "Value", required = true)
        private int value;

        /**
         * Get value.
         *
         * @return value
         */
        public final int getValue() {
            return value;
        }

        /**
         * Set value.
         *
         * @param aValue value
         */
        public final void setValue(final int aValue) {
            this.value = aValue;
        }
    }

    /**
     * Battery data.
     */
    public static class BatteryData extends InputIntegerData {
    }

    /**
     * RSSI data.
     */
    public static class RssiData extends InputIntegerData {
    }

    /**
     * Button data.
     */
    public static class ButtonData extends InputIntegerData {
    }

    /**
     * Pressure data.
     */
    public static class PresureData extends InputIntegerData {
    }

    /**
     * Position data.
     */
    public static class Position extends InputIntegerData {
    }
}
