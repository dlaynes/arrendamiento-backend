package com.grupo2is2.arrendamiento.controller.admin;

import com.grupo2is2.arrendamiento.dto.*;
import com.grupo2is2.arrendamiento.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminController {

    private final AuthService authService;
    private final PropertyService propertyService;
    private final ContractService contractService;
    private final PaymentService paymentService;
    private final DashboardService dashboardService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto dto) {
        return ResponseEntity.ok(authService.updateUser(id, dto));
    }

    @GetMapping("/properties")
    public ResponseEntity<List<PropertyDto>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAll());
    }

    @GetMapping("/contracts")
    public ResponseEntity<List<ContractDto>> getAllContracts() {
        return ResponseEntity.ok(contractService.getAll());
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentDto>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAll());
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }
}
