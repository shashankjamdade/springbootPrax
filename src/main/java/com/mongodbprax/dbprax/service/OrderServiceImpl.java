package com.mongodbprax.dbprax.service;


import com.mongodbprax.dbprax.*;
import com.mongodbprax.dbprax.model.ChatMessageEntity;
import com.mongodbprax.dbprax.repository.ChatMessageRepository;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    private final ChatMessageRepository chatMessageRepository;

    // Store all connected client observers
    private final List<StreamObserver<ChatMessage>> observers = new CopyOnWriteArrayList<>();

    public OrderServiceImpl(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }
    // Unary RPC
    @Override
    public void placeOrder(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        String orderId = UUID.randomUUID().toString();
        OrderResponse response = OrderResponse.newBuilder()
                .setOrderId(orderId)
                .setStatus("CONFIRMED")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Server Streaming
    @Override
    public void trackOrder(OrderIdRequest request, StreamObserver<OrderStatus> responseObserver) {
        String[] updates = {"Order Confirmed", "Preparing Food", "Out for Delivery", "Delivered"};
        for (String u : updates) {
            responseObserver.onNext(OrderStatus.newBuilder().setStatus(u).build());
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
        responseObserver.onCompleted();
    }

    // Client Streaming
    @Override
    public StreamObserver<MenuItem> bulkUpload(StreamObserver<UploadSummary> responseObserver) {
        return new StreamObserver<MenuItem>() {
            int count = 0;

            @Override
            public void onNext(MenuItem menuItem) {
                System.out.println("Received: " + menuItem.getName());
                count++;
            }

            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onCompleted() {
                responseObserver.onNext(UploadSummary.newBuilder().setTotalUploaded(count).build());
                responseObserver.onCompleted();
            }
        };
    }

    // Bi-directional Streaming
    /*@Override
    public StreamObserver<ChatMessage> liveChat(StreamObserver<ChatMessage> responseObserver) {
        return new StreamObserver<ChatMessage>() {
            @Override
            public void onNext(ChatMessage message) {
                System.out.println("Received from " + message.getSender() + ": " + message.getMessage());
                // Echo back
                responseObserver.onNext(ChatMessage.newBuilder()
                        .setSender("Server")
                        .setMessage("Reply to: " + message.getMessage())
                        .build());
            }

            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }*/

    @Override
    public StreamObserver<ChatMessage> liveChat(StreamObserver<ChatMessage> responseObserver) {
        // Add new client observer
        observers.add(responseObserver);

        return new StreamObserver<ChatMessage>() {
            @Override
            public void onNext(ChatMessage message) {
                System.out.println("Received from " + message.getSender() + ": " + message.getMessage());

                // ✅ Save user message in Mongo
                ChatMessageEntity entity = ChatMessageEntity.builder()
                        .conversationId("room1")   // later make dynamic
                        .sender(message.getSender())
                        .message(message.getMessage())
                        .timestamp(Instant.now())
                        .build();
                chatMessageRepository.save(entity);

                // ✅ Broadcast this message to ALL connected clients
                for (StreamObserver<ChatMessage> observer : observers) {
                    try {
                        observer.onNext(ChatMessage.newBuilder()
                                .setSender(message.getSender())
                                .setMessage(message.getMessage())
                                .build());
                    } catch (Exception e) {
                        observers.remove(observer); // remove broken connections
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                observers.remove(responseObserver);
                System.err.println("Chat error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                observers.remove(responseObserver);
                responseObserver.onCompleted();
            }
        };
    }

}
