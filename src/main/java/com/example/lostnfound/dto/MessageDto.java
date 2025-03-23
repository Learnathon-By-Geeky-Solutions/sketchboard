package com.example.lostnfound.dto;

import com.example.lostnfound.enums.MessageReadStatus;
import com.example.lostnfound.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDto {
	@Schema(description = "Id of the Message.", example = "1")
	private Long id;

	@Schema(description = "Sender Id of the Message.", example = "1", required = true)
	private Long senderId;
	@Schema(description = "Sender Name of the Message.", example = "John Doe")
	private String senderName;
	@Schema(description = "Sender Email of the Message.", example = "johnDoe@gmail.com")
	private String senderEmail;

	@Schema(description = "Receiver Id of the Message.", example = "1", required = true)
	private Long receiverId;
	@Schema(description = "Receiver Name of the Message.", example = "Jane Doe")
	private String receiverName;
	@Schema(description = "Receiver Email of the Message.", example = "janeDoe@gmail.com")
	private String receiverEmail;

	@Schema(description = "Created At of the Message.", example = "2021-12-31T12:00:00")
	private LocalDateTime createdAt;
	@Schema(description = "Updated At of the Message.", example = "2021-12-31T12:00:00")
	private LocalDateTime updatedAt;
	@Schema(description = "Read Status of the Message.", example = "SENT")
	private MessageReadStatus readStatus;
	@Schema(description = "Content of the Message.", example = "is this available")
	private String content;
}
