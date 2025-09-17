package com.mongodbprax.dbprax.model.reqres;

import org.hibernate.validator.constraints.NotBlank;

public record LoginReq(@NotBlank String username, @NotBlank String password) {}
