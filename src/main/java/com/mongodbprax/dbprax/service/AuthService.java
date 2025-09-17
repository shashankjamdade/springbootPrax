package com.mongodbprax.dbprax.service;

import com.mongodbprax.dbprax.config.JwtUtil;
import com.mongodbprax.dbprax.model.User;
import com.mongodbprax.dbprax.repository.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepo users;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public AuthService(UserRepo u, JwtUtil j) {
        this.users = u;
        this.jwt = j;
        this.encoder = new BCryptPasswordEncoder();
    }

    public String register(String username, String rawPass) {
        users.findByUsername(username).ifPresent(u -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "taken");
        });
        var u = new User();
        u.setUsername(username);
        u.setPasswordHash(encoder.encode(rawPass));
        users.save(u);
        return jwt.generate(u.getId(), u.getUsername());
    }

    public String login(String username, String rawPass) {
        var u = users.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "bad creds"));
        if (!encoder.matches(rawPass, u.getPasswordHash()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "bad creds");
        return jwt.generate(u.getId(), u.getUsername());
    }
}