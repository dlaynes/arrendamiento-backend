package com.grupo2is2.arrendamiento.controller.landlord;

import com.grupo2is2.arrendamiento.domain.User;
import com.grupo2is2.arrendamiento.dto.*;
import com.grupo2is2.arrendamiento.repository.UserRepository;
import com.grupo2is2.arrendamiento.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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

    @GetMapping("/contracts/{id}")
    public ResponseEntity<ContractDto> getMyContractById(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getById(id, getCurrentUserId()));
    }

    @PutMapping("/contracts/{id}")
    public ResponseEntity<ContractDto> updateContract(@PathVariable Long id, @RequestBody ContractDto dto) {
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
}
