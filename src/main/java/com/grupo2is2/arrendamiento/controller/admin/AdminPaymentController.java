package com.grupo2is2.arrendamiento.controller.admin;

import com.grupo2is2.arrendamiento.dto.PaymentDto;
import com.grupo2is2.arrendamiento.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('PAYMENT_READ')")
public class AdminPaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentDto>> getAll() {
        return ResponseEntity.ok(paymentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PAYMENT_WRITE')")
    public ResponseEntity<PaymentDto> create(@RequestBody PaymentDto dto) {
        return ResponseEntity.ok(paymentService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYMENT_WRITE')")
    public ResponseEntity<PaymentDto> update(@PathVariable Long id, @RequestBody PaymentDto dto) {
        return ResponseEntity.ok(paymentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYMENT_WRITE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
