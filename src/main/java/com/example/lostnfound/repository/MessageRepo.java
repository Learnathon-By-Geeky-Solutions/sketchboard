package com.example.lostnfound.repository;

import com.example.lostnfound.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {

    void findByReceiverId(Long receiverId);

    void findBySenderId(Long senderId);
}