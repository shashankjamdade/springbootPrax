package com.mongodbprax.dbprax.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "stocks")
public class StockEntity {
    @Id
    private String id;
    private String symbol;
    private double price;
    private long timestamp;
    // getters/setters
}
