package org.example.service.impl;

import org.example.common.BusinessException;
import org.example.dto.user.UserLoginDTO;
import org.example.dto.user.UserLoginResponseDTO;
import org.example.dto.user.UserRegisterDTO;
import org.example.dto.user.UserResponseDTO;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDTO register(UserRegisterDTO dto) {
        User existingUser = userMapper.findByUsername(dto.getUsername());
        if (existingUser != null) {
            throw BusinessException.of(400, "用户名已存在");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .nickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername())
                .isLogin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userMapper.insert(user);

        return convertToResponse(user);
    }

    @Override
    @Transactional
    public UserLoginResponseDTO login(UserLoginDTO dto) {
        User user = userMapper.findByUsername(dto.getUsername());
        if (user == null) {
            throw BusinessException.of(400, "用户名或密码错误");
        }

        if (!user.getPassword().equals(dto.getPassword())) {
            throw BusinessException.of(400, "用户名或密码错误");
        }

        user.setIsLogin(true);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        String token = UUID.randomUUID().toString();

        return UserLoginResponseDTO.builder()
                .user(convertToResponse(user))
                .token(token)
                .build();
    }

    @Override
    @Transactional
    public void logout(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setIsLogin(false);
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }

    @Override
    public UserResponseDTO getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.of(404, "用户不存在");
        }
        return convertToResponse(user);
    }

    @Override
    public List<UserResponseDTO> getOnlineUsers() {
        List<User> users = userMapper.findOnlineUsers();
        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private UserResponseDTO convertToResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .isLogin(user.getIsLogin())
                .build();
    }
}