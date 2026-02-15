package io.github.blackspherefollower.buttplug4j.connectors.javax.websocket.server;

import io.github.blackspherefollower.buttplug4j.connectors.javax.websocket.common.ButtplugClientWSEndpoint;

import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * ButtplugClientWSServer using Javax WebSocket.
 */
@ServerEndpoint("/")
public final class ButtplugClientWSServer extends ButtplugClientWSEndpoint {

    /**
     * Constructor.
     *
     * @param clientName client name
     */
    public ButtplugClientWSServer(final String clientName) {
        super(clientName);
        setConnectionState(ConnectionState.CONNECTING);
    }

    @Override
    protected void cleanup() {
        if (getSession() != null) {
            try {
                getSession().close();
            } catch (IOException e) {
                // noop - something when wrong closing the socket, but we're
                // about to dispose of it anyway.
            }
        }
    }
}
