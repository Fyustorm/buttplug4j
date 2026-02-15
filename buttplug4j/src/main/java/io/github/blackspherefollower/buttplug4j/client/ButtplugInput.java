package io.github.blackspherefollower.buttplug4j.client;

/**
 * ButtplugInput enum.
 */
public enum ButtplugInput {
    /**
     * Battery.
     */
    BATTERY("Battery"),
    /**
     * RSSI.
     */
    RSSI("RSSI"),
    /**
     * Button.
     */
    BUTTON("Button"),
    /**
     * Pressure.
     */
    PRESSURE("Pressure"),
    /**
     * Position.
     */
    POSITION("Position");

    /**
     * Input name.
     */
    private final String name;

    /**
     * Constructor.
     *
     * @param aName input name
     */
    ButtplugInput(final String aName) {
        this.name = aName;
    }

    /**
     * Get input from string.
     *
     * @param name input name
     * @return input
     */
    public static ButtplugInput fromString(final String name) {
        for (ButtplugInput input : values()) {
            if (input.name.equalsIgnoreCase(name)) {
                return input;
            }
        }
        throw new IllegalArgumentException("Invalid input type: " + name);
    }

    /**
     * Get input name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }
}
