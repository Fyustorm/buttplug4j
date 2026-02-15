package io.github.blackspherefollower.buttplug4j.client;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.github.blackspherefollower.buttplug4j.protocol.messages.InputReading;

public enum ButtplugInput {
    BATTERY("Battery"),
    RSSI("RSSI"),
    BUTTON("Button"),
    PRESSURE("Pressure"),
    POSITION("Position");

    final private String name;
    ButtplugInput(String name) {
        this.name = name;
    }

    public static ButtplugInput fromString(String name) {
        for (ButtplugInput input : values()) {
            if (input.name.equalsIgnoreCase(name)) {
                return input;
            }
        }
        throw new IllegalArgumentException("Invalid input type: " + name);
    }

    public String getName() {
        return name;
    }
}
