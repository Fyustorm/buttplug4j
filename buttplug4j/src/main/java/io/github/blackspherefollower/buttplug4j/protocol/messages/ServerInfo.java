package io.github.blackspherefollower.buttplug4j.protocol.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugConsts;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugMessage;

/**
 * ServerInfo message.
 */
public final class ServerInfo extends ButtplugMessage {

    /**
     * Protocol version major.
     */
    @JsonProperty(value = "ProtocolVersionMajor", required = true)
    private int protocolVersionMajor;

    /**
     * Protocol version minor.
     */
    @JsonProperty(value = "ProtocolVersionMinor", required = true)
    private int protocolVersionMinor;

    /**
     * Max ping time.
     */
    @JsonProperty(value = "MaxPingTime", required = true)
    private long maxPingTime;

    /**
     * Server name.
     */
    @JsonProperty(value = "ServerName", required = true)
    private String serverName;

    /**
     * Constructor.
     *
     * @param aServerName           server name
     * @param aProtocolVersionMajor major version
     * @param aProtocolVersionMinor minor version
     * @param aMaxPingTime          max ping time
     * @param id                    message ID
     */
    public ServerInfo(final String aServerName, final int aProtocolVersionMajor,
                      final int aProtocolVersionMinor, final long aMaxPingTime, final int id) {
        super(id);

        this.serverName = aServerName;
        this.protocolVersionMajor = aProtocolVersionMajor;
        this.protocolVersionMinor = aProtocolVersionMinor;
        this.maxPingTime = aMaxPingTime;
    }

    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    private ServerInfo() {
        super(ButtplugConsts.DEFAULT_MSG_ID);

        this.serverName = "";
        this.protocolVersionMajor = ButtplugConsts.PROTOCOL_VERSION_MAJOR;
        this.protocolVersionMinor = ButtplugConsts.PROTOCOL_VERSION_MINOR;
        this.maxPingTime = 0;
    }

    /**
     * Get protocol version major.
     *
     * @return major version
     */
    public int getProtocolVersionMajor() {
        return protocolVersionMajor;
    }

    /**
     * Set protocol version major.
     *
     * @param aProtocolVersionMajor major version
     */
    public void setProtocolVersionMajor(final int aProtocolVersionMajor) {
        this.protocolVersionMajor = aProtocolVersionMajor;
    }

    /**
     * Get protocol version minor.
     *
     * @return minor version
     */
    public int getProtocolVersionMinor() {
        return protocolVersionMinor;
    }

    /**
     * Set protocol version minor.
     *
     * @param aProtocolVersionMinor minor version
     */
    public void setProtocolVersionMinor(final int aProtocolVersionMinor) {
        this.protocolVersionMinor = aProtocolVersionMinor;
    }

    /**
     * Get max ping time.
     *
     * @return max ping time
     */
    public long getMaxPingTime() {
        return maxPingTime;
    }

    /**
     * Set max ping time.
     *
     * @param aMaxPingTime max ping time
     */
    public void setMaxPingTime(final long aMaxPingTime) {
        this.maxPingTime = aMaxPingTime;
    }

    /**
     * Get server name.
     *
     * @return server name
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Set server name.
     *
     * @param aServerName server name
     */
    public void setServerName(final String aServerName) {
        this.serverName = aServerName;
    }
}
