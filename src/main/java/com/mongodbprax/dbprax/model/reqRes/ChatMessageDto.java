package com.mongodbprax.dbprax.model.reqRes;

import lombok.Data;

@Data
public class ChatMessageDto {
    private String sender;
    private String message;
}
