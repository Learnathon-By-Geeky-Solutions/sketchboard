package com.example.lostnfound.controller;

import com.example.lostnfound.dto.msgSendBody;
import com.example.lostnfound.enums.MessageReadStatus;
import com.example.lostnfound.model.Message;
import com.example.lostnfound.model.User;
import com.example.lostnfound.service.MessageService;
import com.example.lostnfound.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@Tag(name = "Message APIs", description = "REST APIs related to messages for CRUD operations.Like send, read and update messages.")
public class MessageController {
    private final UserService userService;
    private  final MessageService messageService;

    public MessageController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @PostMapping("/sendMesssage")
    @Operation(summary = "Send Message", description = "Send Message")
    public ResponseEntity<Message> sendMessage(@RequestBody msgSendBody msgPostBody) {
        User sender = userService.getCurrentUser();
        User receiver = userService.findById(msgPostBody.getReceiverId());
        if(sender.getUserId().equals(receiver.getUserId())){
            // return bad request
            return ResponseEntity.status(400).build();
        }

        Message newMessage = new Message();
        newMessage.setSenderId(sender.getUserId());
        newMessage.setReceiverId(msgPostBody.getReceiverId());
        newMessage.setContent(msgPostBody.getContent());
        newMessage.setReadStatus(MessageReadStatus.SENT);
        newMessage.setCreatedAt(LocalDateTime.now());
        newMessage.setUpdatedAt(LocalDateTime.now());
        messageService.save(newMessage);
        //add msg id to sender and receiver
        sender.getMessages().add(newMessage.getId());
        receiver.getMessages().add(newMessage.getId());

        Objects.requireNonNull(receiver.getMessages());
        Objects.requireNonNull(sender.getMessages());

        userService.save(sender);
        userService.save(receiver);
        return ResponseEntity.ok(newMessage);
    }

    @PostMapping("/readMessage")
    @Operation(summary = "Read Message", description = "Read Message")
    public ResponseEntity<Message> readMessage(@RequestBody Long messageId) {
        Message message = messageService.findById(messageId);
        message.setReadStatus(MessageReadStatus.READ);
        messageService.save(message);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/UpdateMessage")
    @Operation(summary = "Update Message", description = "Update Message")
    public ResponseEntity<Void> updateMessage(@RequestBody Long messageId, @RequestBody String content) {
        Message message = messageService.findById(messageId);
        Long CurrentUserId = userService.getCurrentUser().getUserId();
        if (!Objects.equals(message.getSenderId(), CurrentUserId)) {
            // return unauthorized
            return ResponseEntity.status(401).build();
        }
        message.setContent(content);
        message.setUpdatedAt(LocalDateTime.now());
        messageService.save(message);
        return ResponseEntity.ok().build();
    }
}
