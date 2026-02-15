package io.github.blackspherefollower.buttplug4j.client;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.github.blackspherefollower.buttplug4j.protocol.messages.OutputCmd;

public enum ButtplugOutput {
    VIBRATE("Vibrate"),
    ROTATE("Rotate"),
    SPRAY("Spray"),
    OSCILLATE("Oscillate"),
    POSITION("Position"),
    TEMPERATURE("Temperature"),
    CONSTRICT("Constrict"),
    HW_POSITION_WITH_DURATION("HwPositionWithDuration"),
    LED("Led");

    final private String name;
    ButtplugOutput(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ButtplugOutput fromString(String name) {
        for (ButtplugOutput output : values()) {
            if (output.name.equalsIgnoreCase(name)) {
                return output;
            }
        }
        throw new IllegalArgumentException("Invalid output type: " + name);
    }
}
