package com.mongodbprax.dbprax.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "chat_messages")
public class ChatMessageEntity {
    @Id
    private String id;
    private String conversationId; // e.g. orderId or roomId

    private String userId;   // identify which user
    private String sender;   // could be "User" or "Server"
    private String message;
    private Instant timestamp;
}
