package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.dto.ContractDto;

import java.util.List;

public interface ContractService {
    List<ContractDto> getAll();
    ContractDto getById(Long id);
    ContractDto getById(Long id, Long currentUserId);
    List<ContractDto> getByOwner(Long ownerId);
    List<ContractDto> getByTenant(Long tenantId);
    List<ContractDto> getByLandlord(Long landlordId);
    List<ContractDto> getByProperty(Long propertyId);
    ContractDto create(ContractDto dto);
    ContractDto update(Long id, ContractDto dto);
    ContractDto update(Long id, ContractDto dto, Long currentUserId);
    void delete(Long id);
    void delete(Long id, Long currentUserId);
}
