package com.grupo2is2.arrendamiento.controller.tenant;

import com.grupo2is2.arrendamiento.domain.User;
import com.grupo2is2.arrendamiento.dto.*;
import com.grupo2is2.arrendamiento.repository.UserRepository;
import com.grupo2is2.arrendamiento.service.AuthService;
import com.grupo2is2.arrendamiento.service.ContractService;
import com.grupo2is2.arrendamiento.service.DashboardService;
import com.grupo2is2.arrendamiento.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
@PreAuthorize("hasRole('INQUILINO')")
public class TenantController {

    private final UserRepository userRepository;
    private final ContractService contractService;
    private final PaymentService paymentService;
    private final DashboardService dashboardService;
    private final AuthService authService;

    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }

    @PostMapping("/accept-invitation")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthResponse> acceptInvitation(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String name = body.get("name");
        String password = body.get("password");
        return ResponseEntity.ok(authService.acceptInvitation(token, name, password));
    }

    @GetMapping("/contracts")
    public ResponseEntity<List<ContractDto>> getMyContracts() {
        return ResponseEntity.ok(contractService.getByTenant(getCurrentUserId()));
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentDto>> getMyPayments() {
        return ResponseEntity.ok(paymentService.getByTenant(getCurrentUserId()));
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getMyStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }
}
