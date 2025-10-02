package com.mongodbprax.dbprax.repository;


import com.mongodbprax.dbprax.model.ChatMessageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessageEntity, String> {
    List<ChatMessageEntity> findByUserIdOrderByTimestampAsc(String userId);
}
