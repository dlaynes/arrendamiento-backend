package com.grupo2is2.arrendamiento.controller.landlord;

import com.grupo2is2.arrendamiento.dto.PropertyDto;
import com.grupo2is2.arrendamiento.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/landlord/properties")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ARRENDADOR')")
public class LandlordPropertyController extends LandlordBaseController {

    private final PropertyService propertyService;

    @GetMapping("/")
    public ResponseEntity<List<PropertyDto>> getMyProperties() {
        return ResponseEntity.ok(propertyService.getByOwner(getCurrentUserId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDto> getMyPropertyById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getById(id, getCurrentUserId()));
    }

    @PostMapping("/")
    public ResponseEntity<PropertyDto> createProperty(@RequestBody PropertyDto dto) {
        dto.setOwnerId(getCurrentUserId());
        return ResponseEntity.ok(propertyService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyDto> updateProperty(@PathVariable Long id, @RequestBody PropertyDto dto) {
        return ResponseEntity.ok(propertyService.update(id, dto, getCurrentUserId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.delete(id, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}
