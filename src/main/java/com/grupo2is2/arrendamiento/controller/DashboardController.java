package com.grupo2is2.arrendamiento.controller;

import com.grupo2is2.arrendamiento.dto.ContractDto;
import com.grupo2is2.arrendamiento.dto.DashboardStatsDto;
import com.grupo2is2.arrendamiento.dto.PaymentDto;
import com.grupo2is2.arrendamiento.dto.PropertyDto;
import com.grupo2is2.arrendamiento.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getStats(@RequestParam Long userId) {
        return ResponseEntity.ok(dashboardService.getStats(userId));
    }

    @GetMapping("/properties")
    public ResponseEntity<List<PropertyDto>> getMyProperties(@RequestParam Long userId) {
        return ResponseEntity.ok(dashboardService.getMyProperties(userId));
    }

    @GetMapping("/contracts")
    public ResponseEntity<List<ContractDto>> getMyContracts(@RequestParam Long userId) {
        return ResponseEntity.ok(dashboardService.getMyContracts(userId));
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentDto>> getMyPayments(@RequestParam Long userId) {
        return ResponseEntity.ok(dashboardService.getMyPayments(userId));
    }
}

