package io.github.blackspherefollower.buttplug4j.connectors.jetty.websocket.client;

import io.github.blackspherefollower.buttplug4j.client.ButtplugClient;
import io.github.blackspherefollower.buttplug4j.client.ButtplugClientException;
import io.github.blackspherefollower.buttplug4j.client.IConnectedEvent;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugConsts;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugMessage;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugProtocolException;
import io.github.blackspherefollower.buttplug4j.protocol.messages.Error;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

/**
 * ButtplugClientWSClient using Jetty.
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public final class ButtplugClientWSClient extends ButtplugClient {

    /**
     * Connection timeout.
     */
    private static final int TENSEC = 10000;
    /**
     * Jetty websocket client.
     */
    private WebSocketClient client;
    /**
     * Jetty websocket session.
     */
    private Session session;
    /**
     * WebSocket ping timer.
     */
    private Timer wsPingTimer;

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
     * @throws Exception if connection fails
     */
    public void connect(final URI url) throws Exception {

        if (client != null && session != null && session.isOpen()) {
            throw new IllegalStateException("WS is already open");
        }
        setConnectionState(ButtplugClient.ConnectionState.CONNECTING);

        IConnectedEvent stashCallback = getOnConnectedHandler();

        CompletableFuture<Boolean> promise = new CompletableFuture<>();
        setOnConnected(c -> promise.complete(true));

        client = new WebSocketClient();
        client.start();
        client.connect(this, url, new ClientUpgradeRequest()).get();
        promise.get();

        // Restore and echo down the line
        setOnConnected(stashCallback);
        if (stashCallback != null) {
            stashCallback.onConnected(this);
        }
    }

    @Override
    protected void cleanup() {
        if (session != null) {
            session.close();
        }

        LifeCycle.stop(client);
        client = null;
    }

    /**
     * Called when websocket closes.
     *
     * @param statusCode status code
     * @param reason     reason
     */
    @OnWebSocketClose
    public void onClose(final int statusCode, final String reason) {
        this.session = null;
        setConnectionState(ConnectionState.DISCONNECTED);
    }

    /**
     * Called when websocket connects.
     *
     * @param aSession session
     */
    @OnWebSocketConnect
    public void onConnect(final Session aSession) {
        this.session = aSession;

        // Setup websocket ping
        wsPingTimer = new Timer("wsPingTimer", true);
        wsPingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (session != null) {
                        session.getRemote().sendPing(ByteBuffer.wrap("ping".getBytes(StandardCharsets.UTF_8)));
                    }
                } catch (IOException e) {
                    wsPingTimer.cancel();
                    wsPingTimer = null;
                    throw new RuntimeException(e);
                }
            }
        }, 0, TENSEC);

        // Don't block the WS thread
        new Thread(() -> doHandshake()).start();
    }

    /**
     * Called when message received.
     *
     * @param sess    session
     * @param message message
     */
    @OnWebSocketMessage
    public void onMessage(final Session sess, final String message) {
        try {
            List<ButtplugMessage> msgs = getParser().parseJson(message);
            onMessage(msgs);
        } catch (ButtplugProtocolException e) {
            if (getErrorHandler() != null) {
                getErrorHandler().errorReceived(new Error(e));
            } else {
                e.printStackTrace();
            }
        }
    }

    /**
     * Called on websocket error.
     *
     * @param cause cause
     */
    @OnWebSocketError
    public void onWebSocketError(final Throwable cause) {
        if (getErrorHandler() != null) {
            getErrorHandler().errorReceived(new Error(new ButtplugClientException(cause.getMessage())));
        } else {
            cause.printStackTrace();
        }
        new Thread(this::disconnect).start();
    }

    @Override
    protected CompletableFuture<ButtplugMessage> sendMessage(final ButtplugMessage msg) {
        CompletableFuture<ButtplugMessage> promise = scheduleWait(msg.getId(), new CompletableFuture<>());
        if (session == null) {
            Error err = new Error("Bad WS state!",
                    Error.ErrorClass.ERROR_UNKNOWN, ButtplugConsts.SYSTEM_MSG_ID);
            if (getErrorHandler() != null) {
                getErrorHandler().errorReceived(err);
            }
            return CompletableFuture.completedFuture(err);
        }

        try {
            session.getRemote().sendStringByFuture(getParser().formatJson(msg)).get();
        } catch (Exception e) {
            Error err = new Error(new ButtplugClientException(e.getMessage()), msg.getId());
            if (getErrorHandler() != null) {
                getErrorHandler().errorReceived(err);
            }
            return CompletableFuture.completedFuture(err);
        }
        return promise;
    }
}
