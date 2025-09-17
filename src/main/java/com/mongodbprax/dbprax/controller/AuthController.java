package com.mongodbprax.dbprax.controller;

import com.mongodbprax.dbprax.model.reqres.LoginReq;
import com.mongodbprax.dbprax.model.reqres.RegisterReq;
import com.mongodbprax.dbprax.model.reqres.TokenRes;
import com.mongodbprax.dbprax.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService a){ this.auth=a; }

    @PostMapping("/register") public TokenRes register(@Valid @RequestBody RegisterReq r) {
        return new TokenRes(auth.register(r.username(), r.password()));
    }
    @PostMapping("/login") public TokenRes login(@Valid @RequestBody LoginReq r) {
        return new TokenRes(auth.login(r.username(), r.password()));
    }
}