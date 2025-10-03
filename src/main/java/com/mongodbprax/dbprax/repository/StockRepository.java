package com.mongodbprax.dbprax.repository;

import com.mongodbprax.dbprax.model.StockEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StockRepository extends MongoRepository<StockEntity, String> {
    List<StockEntity> findTop5BySymbolOrderByTimestampDesc(String symbol);
}

