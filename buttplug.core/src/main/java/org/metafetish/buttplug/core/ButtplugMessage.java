package org.metafetish.buttplug.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import org.metafetish.buttplug.core.Messages.*;
import org.metafetish.buttplug.core.Messages.Error;

@JsonTypeInfo(include = As.WRAPPER_OBJECT, use = Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceAdded.class, name = "DeviceAdded"),
        @JsonSubTypes.Type(value = DeviceList.class, name = "DeviceList"),
        @JsonSubTypes.Type(value = DeviceRemoved.class, name = "DeviceRemoved"),
        @JsonSubTypes.Type(value = Error.class, name = "Error"),
        @JsonSubTypes.Type(value = LinearCmd.class, name = "LinearCmd"),
        @JsonSubTypes.Type(value = Ok.class, name = "Ok"),
        @JsonSubTypes.Type(value = Ping.class, name = "Ping"),
        @JsonSubTypes.Type(value = RequestDeviceList.class, name = "RequestDeviceList"),
        @JsonSubTypes.Type(value = RequestServerInfo.class, name = "RequestServerInfo"),
        @JsonSubTypes.Type(value = RotateCmd.class, name = "RotateCmd"),
        @JsonSubTypes.Type(value = ScalarCmd.class, name = "ScalarCmd"),
        @JsonSubTypes.Type(value = ScanningFinished.class, name = "ScanningFinished"),
        @JsonSubTypes.Type(value = SensorReadCmd.class, name = "SensorReadCmd"),
        @JsonSubTypes.Type(value = SensorReading.class, name = "SensorReading"),
        @JsonSubTypes.Type(value = SensorSubscribeCmd.class, name = "SensorSubscribeCmd"),
        @JsonSubTypes.Type(value = SensorUnsubscribeCmd.class, name = "SensorUnsubscribeCmd"),
        @JsonSubTypes.Type(value = ServerInfo.class, name = "ServerInfo"),
        @JsonSubTypes.Type(value = StartScanning.class, name = "StartScanning"),
        @JsonSubTypes.Type(value = StopAllDevices.class, name = "StopAllDevices"),
        @JsonSubTypes.Type(value = StopDeviceCmd.class, name = "StopDeviceCmd"),
        @JsonSubTypes.Type(value = StopScanning.class, name = "StopScanning")
})
public abstract class ButtplugMessage {

    @JsonProperty(value = "Id", required = true)
    public long id;

    public ButtplugMessage(long id) {
        this.id = id;
    }
}
