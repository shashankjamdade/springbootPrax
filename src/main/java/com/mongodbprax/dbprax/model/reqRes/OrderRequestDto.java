package com.mongodbprax.dbprax.model.reqRes;


import lombok.Data;

@Data
public class OrderRequestDto {
    private String userId;
    private String itemId;
    private int quantity;
}

