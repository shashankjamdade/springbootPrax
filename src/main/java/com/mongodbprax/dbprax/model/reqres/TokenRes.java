package com.mongodbprax.dbprax.model.reqres;

import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;

public record TokenRes(String token) {}