package org.example.service.impl;

import org.example.dto.message.MessageResponseDTO;
import org.example.dto.message.MessageSendDTO;
import org.example.entity.Message;
import org.example.entity.User;
import org.example.mapper.MessageMapper;
import org.example.mapper.UserMapper;
import org.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public MessageResponseDTO sendMessage(Long fromUserId, MessageSendDTO dto) {
        String msgId = UUID.randomUUID().toString();

        Message message = Message.builder()
                .msgId(msgId)
                .type(dto.getType())
                .fromUserId(fromUserId)
                .toUserId(dto.getToUserId())
                .content(dto.getContent())
                .status("SENT")
                .timestamp(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        messageMapper.insert(message);

        return convertToResponse(message);
    }

    @Override
    public List<MessageResponseDTO> getMessagesByUserId(Long userId, Integer limit) {
        List<Message> messages = messageMapper.findMessagesByUserId(userId, limit);
        return messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponseDTO> getPrivateMessages(Long user1, Long user2, Integer limit) {
        List<Message> messages = messageMapper.findPrivateMessages(user1, user2, limit);
        return messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private MessageResponseDTO convertToResponse(Message message) {
        User fromUser = userMapper.selectById(message.getFromUserId());
        String fromUsername = fromUser != null ? fromUser.getUsername() : "unknown";

        return MessageResponseDTO.builder()
                .msgId(message.getMsgId())
                .type(message.getType())
                .fromUserId(message.getFromUserId())
                .fromUsername(fromUsername)
                .toUserId(message.getToUserId())
                .content(message.getContent())
                .status(message.getStatus())
                .timestamp(message.getTimestamp())
                .build();
    }
}