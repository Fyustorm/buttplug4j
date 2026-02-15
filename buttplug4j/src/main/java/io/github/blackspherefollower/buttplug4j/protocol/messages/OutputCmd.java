package io.github.blackspherefollower.buttplug4j.protocol.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugDeviceMessage;

/**
 * OutputCmd message.
 */
public class OutputCmd extends ButtplugDeviceMessage {

    /**
     * Feature index.
     */
    @JsonProperty(value = "FeatureIndex", required = true)
    private long featureIndex;
    /**
     * Output command.
     */
    @JsonProperty(value = "Command", required = true)
    private IOutputCommand command;

    /**
     * Constructor.
     *
     * @param id            message ID
     * @param deviceIndex   device index
     * @param aFeatureIndex feature index
     */
    public OutputCmd(final int id, final long deviceIndex, final long aFeatureIndex) {
        super(id, deviceIndex);
        this.featureIndex = aFeatureIndex;
    }

    /**
     * Constructor.
     */
    public OutputCmd() {
        super(-1, -1);
        this.featureIndex = -1;
    }

    /**
     * Get feature index.
     *
     * @return index
     */
    public final long getFeatureIndex() {
        return featureIndex;
    }

    /**
     * Set feature index.
     *
     * @param aFeatureIndex index
     */
    public final void setFeatureIndex(final long aFeatureIndex) {
        this.featureIndex = aFeatureIndex;
    }

    /**
     * Get command.
     *
     * @return command
     */
    public final IOutputCommand getCommand() {
        return command;
    }

    /**
     * Set command.
     *
     * @param aCommand command
     */
    public final void setCommand(final IOutputCommand aCommand) {
        this.command = aCommand;
    }

    /**
     * IOutputCommand interface.
     */
    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Vibrate.class, name = "Vibrate"),
            @JsonSubTypes.Type(value = Rotate.class, name = "Rotate"),
            @JsonSubTypes.Type(value = Spray.class, name = "Spray"),
            @JsonSubTypes.Type(value = Oscillate.class, name = "Oscillate"),
            @JsonSubTypes.Type(value = Position.class, name = "Position"),
            @JsonSubTypes.Type(value = Temperature.class, name = "Temperature"),
            @JsonSubTypes.Type(value = Constrict.class, name = "Constrict"),
            @JsonSubTypes.Type(value = HwPositionWithDuration.class, name = "HwPositionWithDuration"),
            @JsonSubTypes.Type(value = Led.class, name = "Led")
    })
    public interface IOutputCommand {
    }

    /**
     * ValueCommand abstract class.
     */
    public abstract static class ValueCommand implements IOutputCommand {
        /**
         * Value.
         */
        @JsonProperty(value = "Value", required = true)
        private int value;

        /**
         * Constructor.
         *
         * @param aValue value
         */
        protected ValueCommand(final int aValue) {
            this.value = aValue;
        }

        /**
         * Constructor.
         */
        protected ValueCommand() {
            this.value = 0;
        }

        /**
         * Get value.
         *
         * @return value
         */
        public int getValue() {
            return value;
        }

        /**
         * Set value.
         *
         * @param aValue value
         */
        public void setValue(final int aValue) {
            this.value = aValue;
        }
    }

    /**
     * Vibrate output.
     */
    public static class Vibrate extends ValueCommand {
        /**
         * Constructor.
         *
         * @param value value
         */
        public Vibrate(final int value) {
            super(value);
        }

        /**
         * Constructor.
         */
        public Vibrate() {
            super(0);
        }
    }

    /**
     * Rotate output.
     */
    public static class Rotate extends ValueCommand {
        /**
         * Constructor.
         *
         * @param value value
         */
        public Rotate(final int value) {
            super(value);
        }

        /**
         * Constructor.
         */
        public Rotate() {
            super(0);
        }
    }

    /**
     * Oscillate output.
     */
    public static class Oscillate extends ValueCommand {
        /**
         * Constructor.
         *
         * @param value value
         */
        public Oscillate(final int value) {
            super(value);
        }

        /**
         * Constructor.
         */
        public Oscillate() {
            super(0);
        }
    }

    /**
     * Constrict output.
     */
    public static class Constrict extends ValueCommand {
        /**
         * Constructor.
         *
         * @param value value
         */
        public Constrict(final int value) {
            super(value);
        }

        /**
         * Constructor.
         */
        public Constrict() {
            super(0);
        }
    }

    /**
     * Spray output.
     */
    public static class Spray extends ValueCommand {
        /**
         * Constructor.
         *
         * @param value value
         */
        public Spray(final int value) {
            super(value);
        }

        /**
         * Constructor.
         */
        public Spray() {
            super(0);
        }
    }

    /**
     * Temperature output.
     */
    public static class Temperature extends ValueCommand {
        /**
         * Constructor.
         *
         * @param value value
         */
        public Temperature(final int value) {
            super(value);
        }

        /**
         * Constructor.
         */
        public Temperature() {
            super(0);
        }
    }

    /**
     * Led output.
     */
    public static class Led extends ValueCommand {
        /**
         * Constructor.
         *
         * @param value value
         */
        public Led(final int value) {
            super(value);
        }

        /**
         * Constructor.
         */
        public Led() {
            super(0);
        }
    }

    /**
     * Position output.
     */
    public static class Position extends ValueCommand {
        /**
         * Constructor.
         *
         * @param value value
         */
        public Position(final int value) {
            super(value);
        }

        /**
         * Constructor.
         */
        public Position() {
            super(0);
        }
    }

    /**
     * HW Position with duration output.
     */
    public static class HwPositionWithDuration extends ValueCommand {
        /**
         * Duration.
         */
        @JsonProperty(value = "Duration", required = true)
        private int duration;

        /**
         * Constructor.
         *
         * @param value     value
         * @param aDuration duration
         */
        public HwPositionWithDuration(final int value, final int aDuration) {
            super(value);
            this.duration = aDuration;
        }

        /**
         * Constructor.
         */
        public HwPositionWithDuration() {
            super();
            this.duration = 0;
        }

        /**
         * Get duration.
         *
         * @return duration
         */
        public int getDuration() {
            return duration;
        }

        /**
         * Set duration.
         *
         * @param aDuration duration
         */
        public void setDuration(final int aDuration) {
            this.duration = aDuration;
        }
    }
}
