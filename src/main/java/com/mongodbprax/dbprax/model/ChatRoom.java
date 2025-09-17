package com.mongodbprax.dbprax.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("rooms")
public class ChatRoom {
    @Id
    String id;
    @Indexed(unique = true)
    String slug; // e.g. "general"
    String name;
    Instant createdAt = Instant.now();
}