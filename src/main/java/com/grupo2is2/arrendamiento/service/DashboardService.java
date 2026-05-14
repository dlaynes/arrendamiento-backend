package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.dto.ContractDto;
import com.grupo2is2.arrendamiento.dto.DashboardStatsDto;
import com.grupo2is2.arrendamiento.dto.PaymentDto;
import com.grupo2is2.arrendamiento.dto.PropertyDto;

import java.util.List;

public interface DashboardService {
    DashboardStatsDto getStats();
    List<PropertyDto> getMyProperties();
    List<ContractDto> getMyContracts();
    List<PaymentDto> getMyPayments();
}
