package com.mongodbprax.dbprax.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "orders")   // MongoDB collection
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEntity {

    @Id
    private String id;        // orderId
    private String userId;
    private String itemId;
    private int quantity;
    private String status;
    private Instant createdAt;
}
