package io.github.blackspherefollower.buttplug4j.client;

/**
 * ButtplugOutput enum.
 */
public enum ButtplugOutput {
    /**
     * Vibrate.
     */
    VIBRATE("Vibrate"),
    /**
     * Rotate.
     */
    ROTATE("Rotate"),
    /**
     * Spray.
     */
    SPRAY("Spray"),
    /**
     * Oscillate.
     */
    OSCILLATE("Oscillate"),
    /**
     * Position.
     */
    POSITION("Position"),
    /**
     * Temperature.
     */
    TEMPERATURE("Temperature"),
    /**
     * Constrict.
     */
    CONSTRICT("Constrict"),
    /**
     * Hardware calculated Position with duration.
     */
    HW_POSITION_WITH_DURATION("HwPositionWithDuration"),
    /**
     * LED.
     */
    LED("Led");

    /**
     * Output name.
     */
    private final String name;

    /**
     * Constructor.
     *
     * @param aName name
     */
    ButtplugOutput(final String aName) {
        this.name = aName;
    }

    /**
     * Get from string.
     *
     * @param name name
     * @return output
     */
    public static ButtplugOutput fromString(final String name) {
        for (ButtplugOutput output : values()) {
            if (output.name.equalsIgnoreCase(name)) {
                return output;
            }
        }
        throw new IllegalArgumentException("Invalid output type: " + name);
    }

    /**
     * Get name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }
}
