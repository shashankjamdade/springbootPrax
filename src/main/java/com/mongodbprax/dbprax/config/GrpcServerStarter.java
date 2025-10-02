package com.mongodbprax.dbprax.config;


import com.mongodbprax.dbprax.service.OrderServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class GrpcServerStarter {

    private final OrderServiceImpl orderServiceImpl;
    private Server server;

    public GrpcServerStarter(OrderServiceImpl orderServiceImpl) {
        this.orderServiceImpl = orderServiceImpl;
    }


    @PostConstruct
    public void startGrpcServer() throws Exception {
        server = ServerBuilder
                .forPort(9090)
                .addService(orderServiceImpl) // your service impl
                .build()
                .start();

        System.out.println("✅ gRPC server started on port 9090");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (server != null) {
                server.shutdown();
            }
        }));
    }
}
