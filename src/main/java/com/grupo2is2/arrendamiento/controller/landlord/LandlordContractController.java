package com.grupo2is2.arrendamiento.controller.landlord;

import com.grupo2is2.arrendamiento.dto.ContractDto;
import com.grupo2is2.arrendamiento.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/landlord/contracts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ARRENDADOR')")
public class LandlordContractController extends LandlordBaseController {

    private final ContractService contractService;

    @GetMapping("/")
    public ResponseEntity<List<ContractDto>> getMyContracts() {
        return ResponseEntity.ok(contractService.getByOwner(getCurrentUserId()));
    }

    @PostMapping("/")
    public ResponseEntity<ContractDto> createContract(@RequestBody ContractDto dto) {
        dto.setLandlordId(getCurrentUserId());
        return ResponseEntity.ok(contractService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractDto> getMyContractById(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getById(id, getCurrentUserId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContractDto> updateContract(@PathVariable Long id, @RequestBody ContractDto dto) {
        dto.setLandlordId(getCurrentUserId());
        return ResponseEntity.ok(contractService.update(id, dto, getCurrentUserId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        contractService.delete(id, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}
