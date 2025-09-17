package com.mongodbprax.dbprax.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

// User.java
@Getter
@Setter
@Document("users")
public class User {
    @Id
    String id;
    @Indexed(unique = true)
    String username;
    String passwordHash;
    Instant createdAt = Instant.now();
    // getters/setters/constructors
}