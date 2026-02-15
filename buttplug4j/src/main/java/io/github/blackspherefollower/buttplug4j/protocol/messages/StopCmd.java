package io.github.blackspherefollower.buttplug4j.protocol.messages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugConsts;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugMessage;

/**
 * StopCmd message.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class StopCmd extends ButtplugMessage {

    /**
     * Stop inputs.
     */
    @JsonProperty(value = "Inputs", required = false)
    private Boolean inputs = null;
    /**
     * Stop outputs.
     */
    @JsonProperty(value = "Outputs", required = false)
    private Boolean outputs = null;
    /**
     * Device index.
     */
    @JsonProperty(value = "DeviceIndex", required = false)
    private Integer deviceIndex = null;
    /**
     * Feature index.
     */
    @JsonProperty(value = "FeatureIndex", required = false)
    private Integer featureIndex = null;

    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    private StopCmd() {
        super(ButtplugConsts.DEFAULT_MSG_ID);
    }

    /**
     * Constructor.
     *
     * @param id message ID
     */
    public StopCmd(final int id) {
        super(id);
    }

    /**
     * Constructor.
     *
     * @param id       message ID
     * @param aInputs  stop inputs
     * @param aOutputs stop outputs
     */
    public StopCmd(final int id, final boolean aInputs, final boolean aOutputs) {
        super(id);
        this.inputs = aInputs;
        this.outputs = aOutputs;
    }

    /**
     * Constructor.
     *
     * @param id           message ID
     * @param aDeviceIndex device index
     */
    public StopCmd(final int id, final int aDeviceIndex) {
        super(id);
        this.deviceIndex = aDeviceIndex;
    }

    /**
     * Constructor.
     *
     * @param id           message ID
     * @param aDeviceIndex device index
     * @param aInputs      stop inputs
     * @param aOutputs     stop outputs
     */
    public StopCmd(final int id, final int aDeviceIndex, final boolean aInputs, final boolean aOutputs) {
        super(id);
        this.deviceIndex = aDeviceIndex;
        this.inputs = aInputs;
        this.outputs = aOutputs;
    }

    /**
     * Constructor.
     *
     * @param id            message ID
     * @param aDeviceIndex  device index
     * @param aFeatureIndex feature index
     */
    public StopCmd(final int id, final int aDeviceIndex, final int aFeatureIndex) {
        super(id);
        this.deviceIndex = aDeviceIndex;
        this.featureIndex = aFeatureIndex;
    }

    /**
     * Constructor.
     *
     * @param id            message ID
     * @param aDeviceIndex  device index
     * @param aFeatureIndex feature index
     * @param aInputs       stop inputs
     * @param aOutputs      stop outputs
     */
    public StopCmd(final int id, final int aDeviceIndex, final int aFeatureIndex,
                   final boolean aInputs, final boolean aOutputs) {
        super(id);
        this.deviceIndex = aDeviceIndex;
        this.featureIndex = aFeatureIndex;
        this.inputs = aInputs;
        this.outputs = aOutputs;
    }

    /**
     * Get inputs.
     *
     * @return inputs
     */
    public Boolean getInputs() {
        return inputs;
    }

    /**
     * Set inputs.
     *
     * @param aInputs inputs
     */
    public void setInputs(final Boolean aInputs) {
        this.inputs = aInputs;
    }

    /**
     * Get outputs.
     *
     * @return outputs
     */
    public Boolean getOutputs() {
        return outputs;
    }

    /**
     * Set outputs.
     *
     * @param aOutputs outputs
     */
    public void setOutputs(final Boolean aOutputs) {
        this.outputs = aOutputs;
    }

    /**
     * Get device index.
     *
     * @return index
     */
    public Integer getDeviceIndex() {
        return deviceIndex;
    }

    /**
     * Set device index.
     *
     * @param aDeviceIndex index
     */
    public void setDeviceIndex(final Integer aDeviceIndex) {
        this.deviceIndex = aDeviceIndex;
    }

    /**
     * Get feature index.
     *
     * @return index
     */
    public Integer getFeatureIndex() {
        return featureIndex;
    }

    /**
     * Set feature index.
     *
     * @param aFeatureIndex index
     */
    public void setFeatureIndex(final Integer aFeatureIndex) {
        this.featureIndex = aFeatureIndex;
    }
}
