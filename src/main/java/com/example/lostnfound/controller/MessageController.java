package com.example.lostnfound.controller;

import com.example.lostnfound.dto.MessageDto;
import com.example.lostnfound.dto.msgSendBody;
import com.example.lostnfound.enums.MessageReadStatus;
import com.example.lostnfound.model.Message;
import com.example.lostnfound.model.User;
import com.example.lostnfound.service.MessageService;
import com.example.lostnfound.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@Tag(name = "Message APIs", description = "REST APIs related to messages for CRUD operations.Like send, read and update messages.")
public class MessageController {
    private final UserService userService;
    private  final MessageService messageService;
    private final ModelMapper modelMapper;

    public MessageController(UserService userService, MessageService messageService, ModelMapper modelMapper) {
        this.userService = userService;
        this.messageService = messageService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/sendMesssage")
    @Operation(summary = "Send Message", description = "Send Message")
    public ResponseEntity<?> sendMessage(@RequestBody msgSendBody msgPostBody) {
        try {
            User sender = userService.getCurrentUser();
            User receiver = userService.findById(msgPostBody.getReceiverId());
            if (sender.getUserId().equals(receiver.getUserId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot send message to self");
            }
            System.out.println("I am here");
            Message newMessage = new Message();
            newMessage.setSenderId(sender.getUserId());
            newMessage.setReceiverId(msgPostBody.getReceiverId());
            newMessage.setContent(msgPostBody.getContent());
            newMessage.setReadStatus(MessageReadStatus.SENT);
            newMessage.setCreatedAt(LocalDateTime.now());
            newMessage.setUpdatedAt(LocalDateTime.now());
            messageService.save(newMessage);
            //add msg id to sender and receiver
            sender.getSent_messages().add(newMessage);
            receiver.getSent_messages().add(newMessage);

            userService.save(sender);
            userService.save(receiver);
            return ResponseEntity.ok(newMessage);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
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

    @GetMapping("/getSentMessages")
    @Operation(summary = "Get messages", description = "Retrieves messages for user")
    public ResponseEntity<?> getMyMessages() {
        try {
            User user = userService.getCurrentUser();
            List<MessageDto> mylist = user.getSent_messages().stream().map(this::msgToMsgDto).toList();
            return new ResponseEntity<>(mylist, HttpStatus.OK);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getReceivedMessages")
    @Operation(summary = "Get received messages", description = "Retrieves received messages for user")
    public ResponseEntity<?> getReceivedMessages() {
        User user = userService.getCurrentUser();
        try{
            List<MessageDto> mylist = user.getReceived_messages().stream().map(message -> {
                MessageDto messageDto = new MessageDto();
                messageDto.setContent(message.getContent());
                messageDto.setCreatedAt(message.getCreatedAt());
                messageDto.setUpdatedAt(message.getUpdatedAt());
                messageDto.setReadStatus(message.getReadStatus());
                messageDto.setReceiverEmail(userService.findById(message.getReceiverId()).getEmail());
                messageDto.setReceiverId(message.getReceiverId());
                messageDto.setReceiverName(userService.findById(message.getReceiverId()).getName());

                messageDto.setSenderId(message.getSenderId());
                messageDto.setSenderEmail(userService.findById(message.getSenderId()).getEmail());
                messageDto.setSenderName(userService.findById(message.getSenderId()).getName());
                return messageDto;
            }).toList();
            return new ResponseEntity<>(mylist, HttpStatus.OK);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private MessageDto msgToMsgDto(Message message) {
        MessageDto messageDto = new MessageDto();
        messageDto.setContent(message.getContent());
        messageDto.setCreatedAt(message.getCreatedAt());
        messageDto.setUpdatedAt(message.getUpdatedAt());
        messageDto.setReadStatus(message.getReadStatus());
        messageDto.setReceiverEmail(userService.findById(message.getReceiverId()).getEmail());
        messageDto.setReceiverId(message.getReceiverId());
        messageDto.setReceiverName(userService.findById(message.getReceiverId()).getName());

        messageDto.setSenderId(message.getSenderId());
        messageDto.setSenderEmail(userService.findById(message.getSenderId()).getEmail());
        messageDto.setSenderName(userService.findById(message.getSenderId()).getName());
        return messageDto;
    }
}
