package com.example.lostnfound.dto;

import com.example.lostnfound.enums.MessageReadStatus;
import com.example.lostnfound.enums.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDto {
	private Long id;

	private Long senderId;
	private String senderName;
	private String senderEmail;

	private Long receiverId;
	private String receiverName;
	private String receiverEmail;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private MessageReadStatus readStatus;
	private String content;
}
