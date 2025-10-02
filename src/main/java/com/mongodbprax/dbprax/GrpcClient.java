package com.mongodbprax.dbprax;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        OrderServiceGrpc.OrderServiceBlockingStub stub =
                OrderServiceGrpc.newBlockingStub(channel);

        // Unary RPC example
        OrderRequest request = OrderRequest.newBuilder()
                .setUserId("user1")
                .setItemId("pizza")
                .setQuantity(2)
                .build();

        OrderResponse response = stub.placeOrder(request);
        System.out.println("Order ID: " + response.getOrderId() + " Status: " + response.getStatus());

        channel.shutdown();
    }
}
