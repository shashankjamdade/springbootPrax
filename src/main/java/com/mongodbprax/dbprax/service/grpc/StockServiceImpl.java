package com.mongodbprax.dbprax.service.grpc;

import com.mongodbprax.dbprax.model.StockEntity;
import com.mongodbprax.dbprax.repository.StockRepository;
import com.playground.grpc.stock.StockPrice;
import com.playground.grpc.stock.StockRequest;
import com.playground.grpc.stock.StockServiceGrpc;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.mongodb.core.messaging.DefaultMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.bson.Document;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest;
import org.springframework.data.mongodb.core.messaging.MessageListener;
import org.springframework.data.mongodb.core.messaging.Subscription;
import org.springframework.data.mongodb.core.messaging.SubscriptionRequest;
import org.springframework.data.mongodb.core.messaging.DefaultMessageListenerContainer;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

@GrpcService
public class StockServiceImpl extends StockServiceGrpc.StockServiceImplBase {

    private final StockRepository repo;
    private final ReactiveMongoTemplate reactiveMongoTemplate;



    public StockServiceImpl(StockRepository repo, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.repo = repo;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public void streamPrices(StockRequest req, StreamObserver<StockPrice> resp) {
        // Cast to ServerCallStreamObserver for lifecycle hooks
        ServerCallStreamObserver<StockPrice> serverObserver =
                (ServerCallStreamObserver<StockPrice>) resp;
        // Create a Flux listening to the "stocks" collection
        Flux<ChangeStreamEvent<StockEntity>> changeStream =
                reactiveMongoTemplate.changeStream(StockEntity.class)
                        .watchCollection("stocks")
                        .listen();

        // Subscribe to the change stream
        Disposable subscription = changeStream.subscribe(event -> {
            StockEntity stock = event.getBody();
            if (stock != null && stock.getSymbol().equalsIgnoreCase(req.getSymbol())) {
                StockPrice msg = StockPrice.newBuilder()
                        .setPrice(stock.getPrice())
                        .setTs(stock.getTimestamp())
                        .build();
                resp.onNext(msg);
            }
        }, error -> {
            resp.onError(error);
        });

        // 👇 Handle client disconnect / cancellation
        serverObserver.setOnCancelHandler(() -> {
            subscription.dispose();   // stop listening to MongoDB
            System.out.println("❌ Client disconnected, subscription disposed.");
        });
    }
}
