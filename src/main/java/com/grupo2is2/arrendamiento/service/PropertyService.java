package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.dto.PropertyDto;

import java.util.List;

public interface PropertyService {
    List<PropertyDto> getAll();
    PropertyDto getById(Long id);
    PropertyDto getById(Long id, Long currentUserId);
    List<PropertyDto> getByOwner(Long ownerId);
    List<PropertyDto> getByTenant(Long tenantId);
    List<PropertyDto> getAvailable();
    PropertyDto create(PropertyDto dto);
    PropertyDto update(Long id, PropertyDto dto);
    PropertyDto update(Long id, PropertyDto dto, Long currentUserId);
    void delete(Long id);
    void delete(Long id, Long currentUserId);
}
