package io.github.blackspherefollower.buttplug4j.connectors.javax.websocket.client;

import io.github.blackspherefollower.buttplug4j.client.ButtplugClient;
import io.github.blackspherefollower.buttplug4j.client.IConnectedEvent;
import io.github.blackspherefollower.buttplug4j.connectors.javax.websocket.common.ButtplugClientWSEndpoint;
import org.eclipse.jetty.util.component.LifeCycle;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * ButtplugClientWSClient using Javax WebSocket.
 */
@ClientEndpoint
public final class ButtplugClientWSClient extends ButtplugClientWSEndpoint {

    /**
     * WebSocket container.
     */
    private WebSocketContainer client;

    /**
     * Constructor.
     *
     * @param clientName client name
     */
    public ButtplugClientWSClient(final String clientName) {
        super(clientName);
    }

    /**
     * Connect to server.
     *
     * @param url server URL
     * @throws IllegalStateException if already open
     * @throws DeploymentException   if deployment fails
     * @throws IOException           if IO error occurs
     * @throws ExecutionException    if execution fails
     * @throws InterruptedException  if interrupted
     */
    public void connect(final URI url) throws IllegalStateException, DeploymentException, IOException,
            ExecutionException, InterruptedException {

        if (client != null && getSession() != null && getSession().isOpen()) {
            throw new IllegalStateException("WS is already open");
        }
        setConnectionState(ButtplugClient.ConnectionState.CONNECTING);

        IConnectedEvent stashCallback = getOnConnectedHandler();

        CompletableFuture<Boolean> promise = new CompletableFuture<>();
        setOnConnected(client -> promise.complete(true));

        client = ContainerProvider.getWebSocketContainer();
        client.connectToServer(this, url);
        promise.get();

        // Restore and echo down the line
        setOnConnected(stashCallback);
        if (stashCallback != null) {
            stashCallback.onConnected(this);
        }
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

        LifeCycle.stop(client);
        client = null;
    }
}
