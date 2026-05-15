package com.grupo2is2.arrendamiento.controller.landlord;

import com.grupo2is2.arrendamiento.dto.PaymentDto;
import com.grupo2is2.arrendamiento.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/landlord/payments")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('PAYMENT_READ')")
public class LandlordPaymentController {
    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<List<PaymentDto>> getMyPayments() {
        return ResponseEntity.ok(dashboardService.getMyPayments());
    }
}
