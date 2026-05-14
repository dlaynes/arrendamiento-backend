package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.dto.AuthResponse;
import com.grupo2is2.arrendamiento.dto.LoginRequest;
import com.grupo2is2.arrendamiento.dto.UserDto;

import java.util.List;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse acceptInvitation(String token, String name, String password);
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    UserDto updateUser(Long id, UserDto dto);
}
