package com.mongodbprax.dbprax.repository;


import com.mongodbprax.dbprax.model.OrderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<OrderEntity, String> {
}
