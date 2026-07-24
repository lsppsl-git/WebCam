package org.example.dto.message;

import lombok.Data;

@Data
public class MessageSendDTO {

    private String type;

    private Long toUserId;

    private String content;
}