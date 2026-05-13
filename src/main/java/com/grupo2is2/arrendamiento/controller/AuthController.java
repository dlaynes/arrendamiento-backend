package com.grupo2is2.arrendamiento.controller;

import com.grupo2is2.arrendamiento.dto.AuthResponse;
import com.grupo2is2.arrendamiento.dto.LoginRequest;
import com.grupo2is2.arrendamiento.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}