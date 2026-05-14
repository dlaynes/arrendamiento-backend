package com.grupo2is2.arrendamiento.controller.landlord;

import com.grupo2is2.arrendamiento.domain.User;
import com.grupo2is2.arrendamiento.domain.UserRole;
import com.grupo2is2.arrendamiento.domain.Property;
import com.grupo2is2.arrendamiento.dto.*;
import com.grupo2is2.arrendamiento.repository.UserRepository;
import com.grupo2is2.arrendamiento.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/landlord")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ARRENDADOR')")
public class LandlordController {

    private final UserRepository userRepository;
    private final PropertyService propertyService;
    private final ContractService contractService;
    private final DashboardService dashboardService;

    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }

    @GetMapping("/properties")
    public ResponseEntity<List<PropertyDto>> getMyProperties() {
        return ResponseEntity.ok(propertyService.getByOwner(getCurrentUserId()));
    }

    @GetMapping("/properties/{id}")
    public ResponseEntity<PropertyDto> getMyPropertyById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getById(id, getCurrentUserId()));
    }

    @PostMapping("/properties")
    public ResponseEntity<PropertyDto> createProperty(@RequestBody PropertyDto dto) {
        dto.setOwnerId(getCurrentUserId());
        return ResponseEntity.ok(propertyService.create(dto));
    }

    @PutMapping("/properties/{id}")
    public ResponseEntity<PropertyDto> updateProperty(@PathVariable Long id, @RequestBody PropertyDto dto) {
        return ResponseEntity.ok(propertyService.update(id, dto, getCurrentUserId()));
    }

    @DeleteMapping("/properties/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.delete(id, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/contracts")
    public ResponseEntity<List<ContractDto>> getMyContracts() {
        return ResponseEntity.ok(contractService.getByOwner(getCurrentUserId()));
    }

    @PostMapping("/contracts")
    public ResponseEntity<ContractDto> createContract(@RequestBody ContractDto dto) {
        dto.setLandlordId(getCurrentUserId());
        return ResponseEntity.ok(contractService.create(dto));
    }

    @GetMapping("/contracts/{id}")
    public ResponseEntity<ContractDto> getMyContractById(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getById(id, getCurrentUserId()));
    }

    @PutMapping("/contracts/{id}")
    public ResponseEntity<ContractDto> updateContract(@PathVariable Long id, @RequestBody ContractDto dto) {
        dto.setLandlordId(getCurrentUserId());
        return ResponseEntity.ok(contractService.update(id, dto, getCurrentUserId()));
    }

    @DeleteMapping("/contracts/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        contractService.delete(id, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentDto>> getMyPayments() {
        return ResponseEntity.ok(dashboardService.getMyPayments());
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getMyStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    @GetMapping("/tenants")
    public ResponseEntity<List<UserDto>> getTenants() {
        List<User> tenants = userRepository.findTenantsByPropertyOwnerId(getCurrentUserId());
        List<UserDto> dtos = tenants.stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .avatar(user.getAvatar())
                .lastLogin(user.getLastLogin())
                .propertyIds(user.getProperties().stream()
                        .map(Property::getId)
                        .collect(Collectors.toList()))
                .build();
    }
}
