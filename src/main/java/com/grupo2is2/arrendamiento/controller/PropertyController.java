package com.grupo2is2.arrendamiento.controller;

import com.grupo2is2.arrendamiento.dto.PropertyDto;
import com.grupo2is2.arrendamiento.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping
    public ResponseEntity<List<PropertyDto>> getAll() {
        return ResponseEntity.ok(propertyService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<PropertyDto>> getByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(propertyService.getByOwner(ownerId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<PropertyDto>> getAvailable() {
        return ResponseEntity.ok(propertyService.getAvailable());
    }

    @PostMapping
    public ResponseEntity<PropertyDto> create(@RequestBody PropertyDto dto) {
        return ResponseEntity.ok(propertyService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyDto> update(@PathVariable Long id, @RequestBody PropertyDto dto) {
        return ResponseEntity.ok(propertyService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        propertyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
