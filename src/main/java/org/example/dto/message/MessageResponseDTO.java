package org.example.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDTO {

    private String msgId;

    private String type;

    private Long fromUserId;

    private String fromUsername;

    private Long toUserId;

    private String content;

    private String status;

    private LocalDateTime timestamp;
}