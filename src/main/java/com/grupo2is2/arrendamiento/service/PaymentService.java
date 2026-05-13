package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.dto.PaymentDto;

import java.util.List;

public interface PaymentService {
    List<PaymentDto> getAll();
    PaymentDto getById(Long id);
    List<PaymentDto> getByContract(Long contractId);
    List<PaymentDto> getByTenant(String tenantName);
    List<PaymentDto> getPending();
    PaymentDto create(PaymentDto dto);
    PaymentDto update(Long id, PaymentDto dto);
    void delete(Long id);
}
