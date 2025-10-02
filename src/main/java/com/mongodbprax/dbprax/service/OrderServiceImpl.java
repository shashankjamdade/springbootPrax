package com.mongodbprax.dbprax.service;


import com.mongodbprax.dbprax.*;
import io.grpc.stub.StreamObserver;
import java.util.UUID;

public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

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
    @Override
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
    }
}
