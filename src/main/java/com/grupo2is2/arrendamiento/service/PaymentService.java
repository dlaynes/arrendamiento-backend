package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.dto.PaymentDto;

import java.util.List;

public interface PaymentService {
    List<PaymentDto> getAll();
    PaymentDto getById(Long id);
    PaymentDto getById(Long id, Long currentUserId);
    List<PaymentDto> getByContract(Long contractId);
    List<PaymentDto> getByTenant(Long tenantId);
    List<PaymentDto> getPending();
    PaymentDto create(PaymentDto dto);
    PaymentDto update(Long id, PaymentDto dto);
    PaymentDto update(Long id, PaymentDto dto, Long currentUserId);
    void delete(Long id);
    void delete(Long id, Long currentUserId);
}
