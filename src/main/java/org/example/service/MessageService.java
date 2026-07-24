package org.example.service;

import org.example.dto.message.MessageResponseDTO;
import org.example.dto.message.MessageSendDTO;

import java.util.List;

public interface MessageService {

    MessageResponseDTO sendMessage(Long fromUserId, MessageSendDTO dto);

    List<MessageResponseDTO> getMessagesByUserId(Long userId, Integer limit);

    List<MessageResponseDTO> getPrivateMessages(Long user1, Long user2, Integer limit);
}