package com.mongodbprax.dbprax.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.mongodbprax.dbprax.OrderServiceGrpc;

@Configuration
public class GrpcClientConfig {

    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder
                .forAddress("localhost", 9090) // gRPC server port
                .usePlaintext()
                .build();
    }

    @Bean
    public OrderServiceGrpc.OrderServiceBlockingStub blockingStub(ManagedChannel channel) {
        return OrderServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    public OrderServiceGrpc.OrderServiceStub asyncStub(ManagedChannel channel) {
        return OrderServiceGrpc.newStub(channel);
    }
}
