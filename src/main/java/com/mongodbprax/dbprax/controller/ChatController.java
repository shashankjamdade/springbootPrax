package com.mongodbprax.dbprax.controller;


import com.mongodbprax.dbprax.model.ChatRoom;
import com.mongodbprax.dbprax.model.Message;
import com.mongodbprax.dbprax.model.User;
import com.mongodbprax.dbprax.model.reqres.MessageView;
import com.mongodbprax.dbprax.model.reqres.SendMessage;
import com.mongodbprax.dbprax.repository.MessageRepo;
import com.mongodbprax.dbprax.repository.RoomRepo;
import com.mongodbprax.dbprax.repository.UserRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rooms")
public class ChatController {
    private final SimpMessagingTemplate broker;
    private final RoomRepo rooms;
    private final MessageRepo messages;
    private final UserRepo users;

    public ChatController(SimpMessagingTemplate b, RoomRepo r, MessageRepo m, UserRepo u) {
        this.broker = b;
        this.rooms = r;
        this.messages = m;
        this.users = u;
    }

    @PostMapping("/crateRoom/{slug}")
    public ChatRoom createRoom(@PathVariable String slug) {
        return rooms.findBySlug(slug)
                .orElseGet(() -> rooms.save(new ChatRoom(null, slug, slug, Instant.now())));
    }


    // REST: last N messages for a room
    @GetMapping("/getRoom/{slug}")
    public List<MessageView> getRoom(@PathVariable String slug,
                                  @RequestParam(defaultValue = "50") int limit) {
        var room = rooms.findBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return messages.findByRoomIdOrderByCreatedAtDesc(room.getId(), PageRequest.of(0, Math.min(200, limit)))
                .stream().map(m -> {
                    var author = users.findById(m.getAuthorId()).map(User::getUsername).orElse("unknown");
                    return new MessageView(m.getId(), slug, author, m.getContent(), m.getCreatedAt());
                }).toList();
    }

    // STOMP: /app/chat.send  -> broadcast to /topic/room.<slug>
    @MessageMapping("/chat.send")
    public void send(SendMessage req, Principal principal, @Header("simpSessionAttributes") Map<String,Object> attrs) {
//        if (principal == null) throw new MessagingException("unauthorized");
//        String userId = (principal != null) ? principal.getName() : (String) attrs.get("user");
//        if (userId == null) throw new MessagingException("unauthorized");

        var room = rooms.findBySlug(req.roomSlug())
                .orElseGet(() -> rooms.save(new ChatRoom(null, req.roomSlug(), req.roomSlug(), Instant.now())));
        var msg = new Message();
        msg.setRoomId(room.getId());
        msg.setAuthorId("123"); // userId
        msg.setContent(req.content());
        msg.setCreatedAt(Instant.now());
        msg = messages.save(msg);

        var username = users.findById(msg.getAuthorId()).map(User::getUsername).orElse("unknown");
        var view = new MessageView(msg.getId(), req.roomSlug(), username, msg.getContent(), msg.getCreatedAt());
        broker.convertAndSend("/topic/room." + req.roomSlug(), view);
    }

}