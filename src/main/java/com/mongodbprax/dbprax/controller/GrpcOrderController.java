package com.mongodbprax.dbprax.controller;


import com.mongodbprax.dbprax.*;
import com.mongodbprax.dbprax.model.ChatMessageEntity;
import com.mongodbprax.dbprax.model.reqRes.ChatMessageDto;
import com.mongodbprax.dbprax.model.reqRes.MenuItemDto;
import com.mongodbprax.dbprax.model.reqRes.OrderRequestDto;
import com.mongodbprax.dbprax.model.reqRes.OrderResponseDto;
import com.mongodbprax.dbprax.repository.ChatMessageRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class GrpcOrderController {

    private final OrderServiceGrpc.OrderServiceBlockingStub blockingStub;
    private final OrderServiceGrpc.OrderServiceStub asyncStub;

    @Autowired
    ChatMessageRepository chatMessageRepository;

    // 1. Unary RPC
    @PostMapping("/place")
    public ResponseEntity<OrderResponseDto> placeOrder(@RequestBody OrderRequestDto req) {
        OrderRequest request = OrderRequest.newBuilder()
                .setUserId(req.getUserId())
                .setItemId(req.getItemId())
                .setQuantity(req.getQuantity())
                .build();

        OrderResponse response = blockingStub.placeOrder(request);
        // map gRPC object -> DTO
        OrderResponseDto dto = new OrderResponseDto(response.getOrderId(), response.getStatus());
        return ResponseEntity.ok(dto);
    }

    // 2. Server Streaming
    @GetMapping("/track/{orderId}")
    public ResponseEntity<List<String>> trackOrder(@PathVariable String orderId) {
        OrderIdRequest request = OrderIdRequest.newBuilder().setOrderId(orderId).build();

        List<String> updates = new ArrayList<>();
        blockingStub.trackOrder(request).forEachRemaining(status -> updates.add(status.getStatus()));

        return ResponseEntity.ok(updates);
    }

    // 3. Client Streaming
    @PostMapping("/bulk-upload")
    public ResponseEntity<String> bulkUpload(@RequestBody List<MenuItemDto> items) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final StringBuilder result = new StringBuilder();

        StreamObserver<MenuItem> requestObserver = asyncStub.bulkUpload(new StreamObserver<UploadSummary>() {
            @Override
            public void onNext(UploadSummary summary) {
                result.append("Total uploaded: ").append(summary.getTotalUploaded());
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        for (MenuItemDto dto : items) {
            requestObserver.onNext(MenuItem.newBuilder().setItemId(dto.getItemId()).setName(dto.getName()).build());
        }
        requestObserver.onCompleted();

        latch.await(5, TimeUnit.SECONDS);
        return ResponseEntity.ok(result.toString());
    }

    // 4. Bi-directional Streaming (simplified as echo test)
  /*  @PostMapping("/chat")
    public ResponseEntity<List<String>> liveChat(@RequestBody List<ChatMessageDto> messages) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        List<String> replies = new ArrayList<>();

        StreamObserver<ChatMessage> requestObserver = asyncStub.liveChat(new StreamObserver<ChatMessage>() {
            @Override
            public void onNext(ChatMessage value) {
                replies.add(value.getSender() + ": " + value.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        for (ChatMessageDto dto : messages) {
            requestObserver.onNext(ChatMessage.newBuilder().setSender(dto.getSender()).setMessage(dto.getMessage()).build());
        }
        requestObserver.onCompleted();

        latch.await(5, TimeUnit.SECONDS);
        return ResponseEntity.ok(replies);
    }*/

    @PostMapping("/chat")
    public ResponseEntity<List<String>> liveChat(@RequestBody List<ChatMessageDto> messages) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        List<String> replies = new ArrayList<>();

        StreamObserver<ChatMessage> requestObserver = asyncStub.liveChat(new StreamObserver<ChatMessage>() {
            @Override
            public void onNext(ChatMessage value) {
                replies.add(value.getSender() + ": " + value.getMessage());

                // ✅ save server reply to Mongo
                ChatMessageEntity replyEntity = ChatMessageEntity.builder()
                        .userId(messages.get(0).getUserId()) // tie reply to same user
                        .sender(value.getSender())
                        .message(value.getMessage())
                        .timestamp(Instant.now())
                        .build();
                chatMessageRepository.save(replyEntity);
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        // ✅ save user messages & forward to gRPC
        for (ChatMessageDto dto : messages) {
            ChatMessageEntity entity = ChatMessageEntity.builder()
                    .userId(dto.getUserId())
                    .sender(dto.getSender())
                    .message(dto.getMessage())
                    .timestamp(Instant.now())
                    .build();
            chatMessageRepository.save(entity);

            requestObserver.onNext(ChatMessage.newBuilder()
                    .setSender(dto.getSender())
                    .setMessage(dto.getMessage())
                    .build());
        }
        requestObserver.onCompleted();

        latch.await(5, TimeUnit.SECONDS);
        return ResponseEntity.ok(replies);
    }





}
