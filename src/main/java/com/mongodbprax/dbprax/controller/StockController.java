package com.mongodbprax.dbprax.controller;

import com.mongodbprax.dbprax.model.StockEntity;
import com.mongodbprax.dbprax.repository.StockRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockRepository repo;

    public StockController(StockRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<?> addStock(@RequestParam String symbol, @RequestParam double price) {
        StockEntity stock = new StockEntity();
        stock.setSymbol(symbol);
        stock.setPrice(price);
        stock.setTimestamp(System.currentTimeMillis());
        repo.save(stock);
        return ResponseEntity.ok("Stock saved");
    }
}
