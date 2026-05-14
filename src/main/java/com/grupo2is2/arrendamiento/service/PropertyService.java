package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.dto.PropertyDto;

import java.util.List;

public interface PropertyService {
    List<PropertyDto> getAll();
    PropertyDto getById(Long id);
    List<PropertyDto> getByOwner(Long ownerId);
    List<PropertyDto> getAvailable();
    PropertyDto create(PropertyDto dto);
    PropertyDto update(Long id, PropertyDto dto);
    void delete(Long id);
}
