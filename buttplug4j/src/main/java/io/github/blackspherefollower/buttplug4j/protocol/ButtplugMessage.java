package io.github.blackspherefollower.buttplug4j.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.github.blackspherefollower.buttplug4j.protocol.messages.DeviceList;
import io.github.blackspherefollower.buttplug4j.protocol.messages.Error;
import io.github.blackspherefollower.buttplug4j.protocol.messages.InputCmd;
import io.github.blackspherefollower.buttplug4j.protocol.messages.InputReading;
import io.github.blackspherefollower.buttplug4j.protocol.messages.Ok;
import io.github.blackspherefollower.buttplug4j.protocol.messages.OutputCmd;
import io.github.blackspherefollower.buttplug4j.protocol.messages.Ping;
import io.github.blackspherefollower.buttplug4j.protocol.messages.RequestDeviceList;
import io.github.blackspherefollower.buttplug4j.protocol.messages.RequestServerInfo;
import io.github.blackspherefollower.buttplug4j.protocol.messages.ScanningFinished;
import io.github.blackspherefollower.buttplug4j.protocol.messages.ServerInfo;
import io.github.blackspherefollower.buttplug4j.protocol.messages.StartScanning;
import io.github.blackspherefollower.buttplug4j.protocol.messages.StopCmd;
import io.github.blackspherefollower.buttplug4j.protocol.messages.StopScanning;

/**
 * ButtplugMessage.
 */
@JsonTypeInfo(include = As.WRAPPER_OBJECT, use = Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceList.class, name = "DeviceList"),
        @JsonSubTypes.Type(value = Error.class, name = "Error"),
        @JsonSubTypes.Type(value = Ok.class, name = "Ok"),
        @JsonSubTypes.Type(value = Ping.class, name = "Ping"),
        @JsonSubTypes.Type(value = RequestDeviceList.class, name = "RequestDeviceList"),
        @JsonSubTypes.Type(value = RequestServerInfo.class, name = "RequestServerInfo"),
        @JsonSubTypes.Type(value = OutputCmd.class, name = "OutputCmd"),
        @JsonSubTypes.Type(value = InputCmd.class, name = "InputCmd"),
        @JsonSubTypes.Type(value = ScanningFinished.class, name = "ScanningFinished"),
        @JsonSubTypes.Type(value = InputReading.class, name = "InputReading"),
        @JsonSubTypes.Type(value = ServerInfo.class, name = "ServerInfo"),
        @JsonSubTypes.Type(value = StartScanning.class, name = "StartScanning"),
        @JsonSubTypes.Type(value = StopCmd.class, name = "StopCmd"),
        @JsonSubTypes.Type(value = StopScanning.class, name = "StopScanning")
})
public abstract class ButtplugMessage {

    /**
     * Message ID.
     */
    @JsonProperty(value = "Id", required = true)
    private int id;

    /**
     * Constructor.
     *
     * @param aId message ID
     */
    public ButtplugMessage(final int aId) {
        this.setId(aId);
    }

    /**
     * Get ID.
     *
     * @return ID
     */
    public final int getId() {
        return id;
    }

    /**
     * Set ID.
     *
     * @param aId ID
     */
    public final void setId(final int aId) {
        this.id = aId;
    }
}
