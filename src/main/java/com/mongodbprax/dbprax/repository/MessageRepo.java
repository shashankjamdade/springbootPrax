package com.mongodbprax.dbprax.repository;

import com.mongodbprax.dbprax.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepo extends MongoRepository<Message, String> {
    Page<Message> findByRoomIdOrderByCreatedAtDesc(String roomId, Pageable pageable);
}