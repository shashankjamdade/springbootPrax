package com.mongodbprax.dbprax.service.grpc;

import com.playground.grpc.chat.ChatMessage;
import com.playground.grpc.chat.ChatServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase {
    @Override
    public StreamObserver<ChatMessage> chat(StreamObserver<ChatMessage> resp) {
        return new StreamObserver<>() {
            @Override public void onNext(ChatMessage msg) {
                resp.onNext(ChatMessage.newBuilder()
                        .setFrom("Server")
                        .setText("Echo: " + msg.getText())
                        .build());
            }
            @Override public void onError(Throwable t) {}
            @Override public void onCompleted() { resp.onCompleted(); }
        };
    }
}
