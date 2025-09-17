package com.mongodbprax.dbprax.model.reqres;

import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;

public record RegisterReq(@NotBlank String username, @NotBlank @Size(min=8,max=64) String password) {}
