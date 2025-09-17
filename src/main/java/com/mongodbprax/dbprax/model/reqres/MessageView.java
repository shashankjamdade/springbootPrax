package com.mongodbprax.dbprax.model.reqres;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


public record MessageView(String id, String roomSlug, String author, String content, Instant createdAt) {}
