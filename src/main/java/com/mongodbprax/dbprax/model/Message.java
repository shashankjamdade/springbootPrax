package com.mongodbprax.dbprax.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

// Message.java
@Getter
@Setter
@Document("messages")
@CompoundIndexes({
        @CompoundIndex(name="room_ts_idx", def="{ 'roomId': 1, 'createdAt': -1 }")
})
public class Message {
    @Id
    String id;
    @Indexed
    String roomId;
    @Indexed String authorId;
    String content;          // keep <2KB; enforce on DTO
    Instant createdAt = Instant.now();
}