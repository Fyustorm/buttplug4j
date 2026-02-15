package io.github.blackspherefollower.buttplug4j.protocol.messages;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * InputCommandType enum.
 */
public enum InputCommandType {
    /**
     * Read.
     */
    READ("Read"),
    /**
     * Subscribe.
     */
    SUBSCRIBE("Subscribe"),
    /**
     * Unsubscribe.
     */
    UNSUBSCRIBE("Unsubscribe");

    /**
     * Specification name.
     */
    private final String specName;

    /**
     * Constructor.
     *
     * @param aSpecName spec name
     */
    InputCommandType(final String aSpecName) {
        this.specName = aSpecName;
    }

    /**
     * Get spec name.
     *
     * @return spec name
     */
    @JsonValue
    public String getSpecName() {
        return specName;
    }
}
