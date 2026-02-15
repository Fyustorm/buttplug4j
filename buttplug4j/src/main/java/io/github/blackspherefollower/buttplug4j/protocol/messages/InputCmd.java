package io.github.blackspherefollower.buttplug4j.protocol.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugDeviceMessage;

/**
 * InputCmd message.
 */
public class InputCmd extends ButtplugDeviceMessage {

    /**
     * Feature index.
     */
    @JsonProperty(value = "FeatureIndex", required = true)
    private int featureIndex;

    /**
     * Input type.
     */
    @JsonProperty(value = "Type", required = true)
    private String inputType;

    /**
     * Input command.
     */
    @JsonProperty(value = "Command", required = true)
    private InputCommandType inputCommand;

    /**
     * Constructor.
     *
     * @param id            message ID
     * @param deviceIndex   device index
     * @param aFeatureIndex feature index
     * @param aInputType    input type
     * @param aInputCommand command
     */
    public InputCmd(final int id, final long deviceIndex, final int aFeatureIndex,
                    final String aInputType, final InputCommandType aInputCommand) {
        super(id, deviceIndex);
        this.featureIndex = aFeatureIndex;
        this.inputType = aInputType;
        this.inputCommand = aInputCommand;
    }

    /**
     * Constructor.
     */
    public InputCmd() {
        super(-1, -1);
        this.featureIndex = -1;
        this.inputType = "None";
        this.inputCommand = InputCommandType.READ;
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
     * Get input type.
     *
     * @return type
     */
    public final String getInputType() {
        return inputType;
    }

    /**
     * Set input type.
     *
     * @param aInputType type
     */
    public final void setInputType(final String aInputType) {
        this.inputType = aInputType;
    }

    /**
     * Get input command.
     *
     * @return command
     */
    public final InputCommandType getInputCommand() {
        return inputCommand;
    }

    /**
     * Set input command.
     *
     * @param aInputCommand command
     */
    public final void setInputCommand(final InputCommandType aInputCommand) {
        this.inputCommand = aInputCommand;
    }
}
