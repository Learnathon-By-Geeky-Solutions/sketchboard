package com.example.lostnfound.service;

import com.example.lostnfound.enums.MessageReadStatus;
import com.example.lostnfound.model.Message;
import com.example.lostnfound.repository.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageService {
    private final MessageRepo messageRepo;

    @Autowired
    public MessageService(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    public void save(Message message) {
        messageRepo.save(message);
    }

    public void delete(Long id) {
        messageRepo.deleteById(id);
    }

    public Message get(Long id) {
        return messageRepo.findById(id).orElse(null);
    }

    public void update(Long id, Message message) {
        Message existingMessage = messageRepo.findById(id).orElse(null);
        if (existingMessage != null) {
            existingMessage.setContent(message.getContent());
            existingMessage.setReadStatus(MessageReadStatus.SENT);
            existingMessage.setUpdatedAt(LocalDateTime.now());
            messageRepo.save(existingMessage);
        }
    }

    public void getReceivedMessages(Long receiverId) {
        messageRepo.findByReceiverId(receiverId);
    }

    public void getSentMessages(Long senderId) {
        messageRepo.findBySenderId(senderId);
    }

    public Message findById(Long messageId) {
        return messageRepo.findById(messageId).orElse(null);
    }
}
