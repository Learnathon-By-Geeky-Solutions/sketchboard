package com.example.lostnfound.service;

import com.example.lostnfound.enums.MessageReadStatus;
import com.example.lostnfound.model.Message;
import com.example.lostnfound.repository.MessageRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    @Mock
    private MessageRepo messageRepo;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveMessage() {
        Message message = new Message();
        message.setContent("Hello");

        messageService.save(message);
        verify(messageRepo, times(1)).save(message);
    }

    @Test
    void testDeleteMessage() {
        Long id = 1L;
        messageService.delete(id);
        verify(messageRepo, times(1)).deleteById(id);
    }

    @Test
    void testGetMessageFound() {
        Message msg = new Message();
        msg.setId(1L);
        msg.setContent("Test");

        when(messageRepo.findById(1L)).thenReturn(Optional.of(msg));

        Message found = messageService.get(1L);
        assertNotNull(found);
        assertEquals("Test", found.getContent());
    }

    @Test
    void testGetMessageNotFound() {
        when(messageRepo.findById(1L)).thenReturn(Optional.empty());
        Message result = messageService.get(1L);
        assertNull(result);
    }

    @Test
    void testUpdateMessage() {
        Message existing = new Message();
        existing.setId(1L);
        existing.setContent("Old content");

        Message updated = new Message();
        updated.setContent("New content");

        when(messageRepo.findById(1L)).thenReturn(Optional.of(existing));

        messageService.update(1L, updated);

        assertEquals("New content", existing.getContent());
        assertEquals(MessageReadStatus.SENT, existing.getReadStatus());
        assertNotNull(existing.getUpdatedAt());

        verify(messageRepo).save(existing);
    }

    @Test
    void testUpdateMessage_NotFound() {
        when(messageRepo.findById(1L)).thenReturn(Optional.empty());

        Message updated = new Message();
        updated.setContent("New");

        messageService.update(1L, updated);
        verify(messageRepo, never()).save(any(Message.class));
    }

    @Test
    void testGetReceivedMessages() {
        Long receiverId = 1L;
        messageService.getReceivedMessages(receiverId);
        verify(messageRepo, times(1)).findByReceiverId(receiverId);
    }

    @Test
    void testGetSentMessages() {
        Long senderId = 1L;
        messageService.getSentMessages(senderId);
        verify(messageRepo, times(1)).findBySenderId(senderId);
    }

    @Test
    void testFindById() {
        Message message = new Message();
        message.setId(2L);
        when(messageRepo.findById(2L)).thenReturn(Optional.of(message));

        Message result = messageService.findById(2L);
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }
}
