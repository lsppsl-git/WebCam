package org.example.controller;

import jakarta.validation.Valid;
import org.example.common.Result;
import org.example.dto.user.UserLoginDTO;
import org.example.dto.user.UserLoginResponseDTO;
import org.example.dto.user.UserRegisterDTO;
import org.example.dto.user.UserResponseDTO;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<UserResponseDTO> register(@Valid @RequestBody UserRegisterDTO dto) {
        UserResponseDTO user = userService.register(dto);
        return Result.success("注册成功", user);
    }

    @PostMapping("/login")
    public Result<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginDTO dto) {
        UserLoginResponseDTO response = userService.login(dto);
        return Result.success("登录成功", response);
    }

    @PostMapping("/logout/{userId}")
    public Result<String> logout(@PathVariable Long userId) {
        userService.logout(userId);
        return Result.success("登出成功");
    }

    @GetMapping("/{userId}")
    public Result<UserResponseDTO> getUserById(@PathVariable Long userId) {
        UserResponseDTO user = userService.getUserById(userId);
        return Result.success(user);
    }

    @GetMapping("/online")
    public Result<List<UserResponseDTO>> getOnlineUsers() {
        List<UserResponseDTO> users = userService.getOnlineUsers();
        return Result.success(users);
    }
}