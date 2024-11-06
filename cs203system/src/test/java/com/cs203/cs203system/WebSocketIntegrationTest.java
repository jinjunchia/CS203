package com.cs203.cs203system;

import com.cs203.cs203system.config.WebSocket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketIntegrationTest {

    @Autowired
    private WebSocket webSocketConfig;

    private WebSocketStompClient stompClient;
    private BlockingQueue<String> blockingQueue;

    @BeforeEach
    public void setup() {
        stompClient = new WebSocketStompClient(
                new SockJsClient(Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        blockingQueue = new LinkedBlockingQueue<>();
    }

    @Test
    public void testWebSocketConnection() throws Exception {
        StompSession session = stompClient
                .connect("ws://localhost:8080/ws", new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        assertNotNull(session);
        session.subscribe("/user/queue/test", new DefaultStompFrameHandler());

        session.send("/app/message", "Hello WebSocket");

        String receivedMessage = blockingQueue.poll(3, TimeUnit.SECONDS);
        assertNotNull(receivedMessage);
        assertEquals("Hello WebSocket", receivedMessage);
    }

    private class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(org.springframework.messaging.simp.stomp.StompHeaders stompHeaders) {
            return String.class;
        }

        @Override
        public void handleFrame(org.springframework.messaging.simp.stomp.StompHeaders stompHeaders, Object o) {
            blockingQueue.offer((String) o);
        }
    }
}
