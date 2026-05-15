package com.grupo2is2.arrendamiento.controller.admin;

import com.grupo2is2.arrendamiento.dto.ContractDto;
import com.grupo2is2.arrendamiento.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/contracts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminContractController {

    private final ContractService contractService;

    @GetMapping
    public ResponseEntity<List<ContractDto>> getAll() {
        return ResponseEntity.ok(contractService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getById(id));
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<ContractDto>> getByProperty(@PathVariable Long propertyId) {
        return ResponseEntity.ok(contractService.getByProperty(propertyId));
    }

    @PostMapping
    public ResponseEntity<ContractDto> create(@RequestBody ContractDto dto) {
        return ResponseEntity.ok(contractService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContractDto> update(@PathVariable Long id, @RequestBody ContractDto dto) {
        return ResponseEntity.ok(contractService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contractService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
