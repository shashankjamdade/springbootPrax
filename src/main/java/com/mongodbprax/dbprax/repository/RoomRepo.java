package com.mongodbprax.dbprax.repository;

import com.mongodbprax.dbprax.model.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoomRepo extends MongoRepository<ChatRoom, String> {
    Optional<ChatRoom> findBySlug(String slug);
}