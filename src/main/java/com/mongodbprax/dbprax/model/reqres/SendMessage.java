package com.mongodbprax.dbprax.model.reqres;

import jakarta.validation.constraints.Size;

public record SendMessage(String roomSlug, @Size(min=1, max=2000) String content) {}

