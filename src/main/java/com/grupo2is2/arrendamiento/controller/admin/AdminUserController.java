package com.grupo2is2.arrendamiento.controller.admin;

import com.grupo2is2.arrendamiento.dto.UserDto;
import com.grupo2is2.arrendamiento.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('USER_READ')")
public class AdminUserController {

    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UserDto dto) {
        return ResponseEntity.ok(authService.updateUser(id, dto));
    }
}
