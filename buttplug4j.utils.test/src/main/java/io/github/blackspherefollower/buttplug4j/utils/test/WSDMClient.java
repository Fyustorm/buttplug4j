package io.github.blackspherefollower.buttplug4j.utils.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class WSDMClient {

    private final WSDHeader header;
    private final CompletableFuture<Boolean> connected = new CompletableFuture<>();
    private final ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();
    private int battery = 100;
    private WebSocket client;

    public WSDMClient(final URI url, final String identifier, final String address) throws Exception {
        header = new WSDHeader();
        header.identifier = identifier;
        header.address = address;

        HttpClient
                .newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(url, new WebSocketClient(this))
                .join();
        connected.get(10, TimeUnit.SECONDS);

    }

    public final ConcurrentLinkedQueue<String> getMessages() {
        return messages;
    }

    public final void setBattery(final int battery) {
        this.battery = battery;
    }

    public void onClose(final int statusCode, final String reason) {
        this.client = null;
    }

    public void onConnect(final WebSocket client) {
        this.client = client;
        // Don't block the WS thread
        new Thread(() -> {
            try {
                client.sendText(new ObjectMapper().writeValueAsString(header), true).get(1, TimeUnit.SECONDS);
            } catch (JsonProcessingException | ExecutionException | InterruptedException | TimeoutException e) {
                System.out.println("Failed to send header: " + e.getMessage());
            }
        }).start();
    }

    public void onMessage(final String message) {
        System.out.println("Got message: " + message);
        if (message.startsWith("DeviceType;")) {
            new Thread(() -> {
                try {
                    sendMessage("Z:10:" + header.address + ";");
                    if (!connected.isDone()) {
                        connected.complete(true);
                    }
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    throw new RuntimeException(e);
                }

            }).start();
            return;
        }
        if (message.startsWith("Battery;")) {
            new Thread(() -> {
                try {
                    sendMessage(battery + ";");
                    connected.complete(true);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    throw new RuntimeException(e);
                }

            }).start();
            return;
        }
        messages.add(message);
    }

    protected void sendMessage(final String msg) throws ExecutionException, InterruptedException, TimeoutException {
        client.sendBinary(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)), true).get(1, TimeUnit.SECONDS);
    }

    static class WSDHeader {
        @JsonProperty("identifier")
        private String identifier;
        @JsonProperty("address")
        private String address;
        @JsonProperty("version")
        private int version = 0;
    }

    private static class WebSocketClient implements WebSocket.Listener {
        private final WSDMClient wsdmclient;

        WebSocketClient(final WSDMClient wsdmclient) {
            this.wsdmclient = wsdmclient;
        }

        @Override
        public void onOpen(final WebSocket webSocket) {
            System.out.println("onOpen using subprotocol " + webSocket.getSubprotocol());
            wsdmclient.onConnect(webSocket);
            WebSocket.Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onText(final WebSocket webSocket, final CharSequence data, final boolean last) {
            System.out.println("onText received " + data);
            wsdmclient.onMessage(data.toString());
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        @Override
        public void onError(final WebSocket webSocket, final Throwable error) {
            System.out.println("Bad day! " + webSocket.toString());
            error.printStackTrace();
            WebSocket.Listener.super.onError(webSocket, error);
        }

        @Override
        public CompletionStage<?> onClose(final WebSocket webSocket, final int statusCode, final String reason) {
            System.out.println("onClose received " + statusCode + " " + reason);
            wsdmclient.onClose(statusCode, reason);
            return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
        }

        @Override
        public CompletionStage<?> onBinary(final WebSocket webSocket, final ByteBuffer message, final boolean last) {
            System.out.println("onBinary received " + message);
            wsdmclient.onMessage(StandardCharsets.UTF_8.decode(message).toString());
            return WebSocket.Listener.super.onBinary(webSocket, message, last);
        }
    }
}
