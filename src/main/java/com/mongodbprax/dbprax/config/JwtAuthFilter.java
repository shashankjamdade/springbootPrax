package com.mongodbprax.dbprax.config;

import com.mongodbprax.dbprax.repository.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwt;
    private final UserRepo users;

    public JwtAuthFilter(JwtUtil jwt, UserRepo users) {
        this.jwt = jwt;
        this.users = users;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip auth endpoints
        String path = request.getServletPath();
        return path.startsWith("/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws IOException, ServletException {

        String hdr = req.getHeader("Authorization");
        if (hdr != null && hdr.startsWith("Bearer ")) {
            String token = hdr.substring(7);
            try {
                var jws = jwt.parse(token);
                String userId = jws.getBody().getSubject();

                users.findById(userId).ifPresent(user -> {
                    var auth = new UsernamePasswordAuthenticationToken(
                            user.getId(), null, List.of()
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });

            } catch (Exception e) {
                // log the error for debugging
                System.err.println("JWT validation failed: " + e.getMessage());
            }
        }

        chain.doFilter(req, res);
    }
}
