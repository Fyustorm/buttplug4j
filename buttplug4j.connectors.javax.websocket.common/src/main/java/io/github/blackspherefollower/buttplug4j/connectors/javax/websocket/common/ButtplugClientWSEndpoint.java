package io.github.blackspherefollower.buttplug4j.connectors.javax.websocket.common;

import io.github.blackspherefollower.buttplug4j.client.ButtplugClient;
import io.github.blackspherefollower.buttplug4j.client.ButtplugClientException;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugConsts;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugMessage;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugProtocolException;
import io.github.blackspherefollower.buttplug4j.protocol.messages.Error;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

/**
 * ButtplugClientWSEndpoint.
 */
@ClientEndpoint
@ServerEndpoint("/")
public abstract class ButtplugClientWSEndpoint extends ButtplugClient {
    /**
     * Ping timeout.
     */
    private static final int TENSEC = 10000;
    /**
     * Websocket session.
     */
    private Session session;
    /**
     * Websocket ping timer.
     */
    private Timer wsPingTimer;

    /**
     * Constructor.
     *
     * @param aClientName client name
     */
    public ButtplugClientWSEndpoint(final String aClientName) {
        super(aClientName);
    }

    /**
     * Get session.
     *
     * @return session
     */
    public final Session getSession() {
        return session;
    }

    /**
     * Called when message received.
     *
     * @param sess    session
     * @param message message
     */
    @OnMessage
    public final void onMessage(final Session sess, final String message) {
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
     * Called when websocket closes.
     *
     * @param reason reason
     */
    @OnClose
    @SuppressWarnings("unused")
    public final void onClose(final CloseReason reason) {
        this.session = null;
        setConnectionState(ConnectionState.DISCONNECTED);
    }

    /**
     * Called when websocket connects.
     *
     * @param newSession session
     */
    @OnOpen
    @SuppressWarnings("unused")
    public final void onConnect(final Session newSession) {
        this.session = newSession;

        // Setup websocket ping
        wsPingTimer = new Timer("wsPingTimer", true);
        wsPingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (session != null) {
                        session.getAsyncRemote().sendPing(ByteBuffer.wrap("ping".getBytes(StandardCharsets.UTF_8)));
                    }
                } catch (IOException e) {
                    wsPingTimer.cancel();
                    wsPingTimer = null;
                    throw new RuntimeException(e);
                }
            }
        }, 0, TENSEC);

        // Don't block the WS thread
        new Thread(this::doHandshake).start();
    }

    /**
     * Called on websocket error.
     *
     * @param cause cause
     */
    @OnError
    public final void onWebSocketError(final Throwable cause) {
        if (getErrorHandler() != null) {
            getErrorHandler().errorReceived(new Error(new ButtplugClientException(cause.getMessage())));
        } else {
            cause.printStackTrace();
        }
        disconnect();
    }

    @Override
    protected final CompletableFuture<ButtplugMessage> sendMessage(final ButtplugMessage msg) {
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
            session.getAsyncRemote().sendText(getParser().formatJson(msg)).get();
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
