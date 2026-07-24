package org.example.service;

import org.example.dto.user.UserLoginDTO;
import org.example.dto.user.UserLoginResponseDTO;
import org.example.dto.user.UserRegisterDTO;
import org.example.dto.user.UserResponseDTO;

import java.util.List;

public interface UserService {

    UserResponseDTO register(UserRegisterDTO dto);

    UserLoginResponseDTO login(UserLoginDTO dto);

    void logout(Long userId);

    UserResponseDTO getUserById(Long userId);

    List<UserResponseDTO> getOnlineUsers();
}