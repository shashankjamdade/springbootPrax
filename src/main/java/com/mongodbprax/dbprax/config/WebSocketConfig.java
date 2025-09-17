package com.mongodbprax.dbprax.config;

import com.mongodbprax.dbprax.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

// WebSocketConfig.java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired JwtUtil jwt; @Autowired
    UserRepo users;

    @Override public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");   // broadcasts
        config.setApplicationDestinationPrefixes("/app"); // client sends here
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new WsChannelConfig(jwt));
    }


    @Override public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")      // tighten for prod
                .withSockJS();

        // handshake JWT via query: /ws?token=JWT
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*")
                .addInterceptors(new HttpSessionHandshakeInterceptor(){
                    @Override public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse res,
                                                             WebSocketHandler wsHandler, Map<String, Object> attrs) {
                        var uri = req.getURI();
                        var query = uri.getQuery();
                        if (query != null && query.startsWith("token=")) {
                            var token = query.substring("token=".length());
                            try {
                                var jws = jwt.parse(token);
                                attrs.put("userId", jws.getBody().getSubject());
                                return true;
                            } catch(Exception e){ res.setStatusCode(HttpStatus.UNAUTHORIZED); return false; }
                        }
                        res.setStatusCode(HttpStatus.UNAUTHORIZED); return false;
                    }
                });
    }
}
