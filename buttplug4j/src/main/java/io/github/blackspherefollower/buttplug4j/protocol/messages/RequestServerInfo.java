package io.github.blackspherefollower.buttplug4j.protocol.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugConsts;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugMessage;

import static io.github.blackspherefollower.buttplug4j.protocol.ButtplugConsts.PROTOCOL_VERSION_MAJOR;
import static io.github.blackspherefollower.buttplug4j.protocol.ButtplugConsts.PROTOCOL_VERSION_MINOR;

/**
 * RequestServerInfo message.
 */
public final class RequestServerInfo extends ButtplugMessage {
    /**
     * Protocol version major.
     */
    @JsonProperty(value = "ProtocolVersionMajor", required = true)
    private final long protocolVersionMajor = PROTOCOL_VERSION_MAJOR;

    /**
     * Protocol version minor.
     */
    @JsonProperty(value = "ProtocolVersionMinor", required = true)
    private final long protocolVersionMinor = PROTOCOL_VERSION_MINOR;

    /**
     * Client name.
     */
    @JsonProperty(value = "ClientName", required = true)
    private String clientName;

    /**
     * Constructor.
     *
     * @param aClientName client name
     * @param id          message ID
     */
    public RequestServerInfo(final String aClientName, final int id) {
        super(id);
        this.setClientName(aClientName);
    }

    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    private RequestServerInfo() {
        super(ButtplugConsts.DEFAULT_MSG_ID);
        this.setClientName("");
    }

    /**
     * Get protocol version major.
     *
     * @return major version
     */
    public long getProtocolVersionMajor() {
        return protocolVersionMajor;
    }

    /**
     * Get protocol version minor.
     *
     * @return minor version
     */
    public long getProtocolVersionMinor() {
        return protocolVersionMinor;
    }

    /**
     * Get client name.
     *
     * @return client name
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Set client name.
     *
     * @param aClientName client name
     */
    public void setClientName(final String aClientName) {
        this.clientName = aClientName;
    }
}
