package org.example.controller;

import org.example.dto.message.MessageResponseDTO;
import org.example.dto.message.MessageSendDTO;
import org.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class WebSocketController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/send")
    public void handleChatMessage(Map<String, Object> payload) {
        Long fromUserId = ((Number) payload.get("fromUserId")).longValue();
        MessageSendDTO dto = new MessageSendDTO();
        dto.setType((String) payload.get("type"));
        dto.setToUserId(((Number) payload.get("toUserId")).longValue());
        dto.setContent((String) payload.get("content"));

        MessageResponseDTO response = messageService.sendMessage(fromUserId, dto);

        messagingTemplate.convertAndSend("/queue/user/" + dto.getToUserId(), response);
    }

    @MessageMapping("/chat/group")
    @SendTo("/topic/group")
    public MessageResponseDTO handleGroupMessage(Map<String, Object> payload) {
        Long fromUserId = ((Number) payload.get("fromUserId")).longValue();
        MessageSendDTO dto = new MessageSendDTO();
        dto.setType("GROUP");
        dto.setToUserId(0L);
        dto.setContent((String) payload.get("content"));

        return messageService.sendMessage(fromUserId, dto);
    }
}