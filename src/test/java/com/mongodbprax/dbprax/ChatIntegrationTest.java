package com.mongodbprax.dbprax;

import com.mongodbprax.dbprax.model.reqres.SendMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;
import com.mongodbprax.dbprax.model.reqres.SendMessage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.mongodbprax.dbprax.model.reqres.SendMessage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.mongodbprax.dbprax.model.reqres.SendMessage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void testChat() throws Exception {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        CountDownLatch latch = new CountDownLatch(1);

        // ✅ New API: connectAsync instead of connect()
        CompletableFuture<StompSession> future = stompClient.connectAsync(
                "ws://localhost:" + port + "/ws?token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2OGM4OTkyOTg1YjExYjFiMTQ5ZDZhZjYiLCJ1IjoiYWxpY2UzIiwiaWF0IjoxNzU3OTc3MzMwLCJleHAiOjE3NjA1NjkzMzB9.T2WYCd0GNhxP7vMDWUhxlj3WPZ9C6njVNfGIOmslJj4",
                new StompSessionHandlerAdapter() {}
        );

        StompSession session = future.get(5, TimeUnit.SECONDS);

        // subscribe
        session.subscribe("/topic/room.general", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class; // Or MessageView.class if you want full objects
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("Received: " + payload);
                latch.countDown();
            }
        });

        // send message
        session.send("/app/chat.send", new SendMessage("general", "hello from Spring 6 test"));

        // wait for message
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new AssertionError("No message received in time");
        }
    }
}
