package com.grupo2is2.arrendamiento.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsDto {
    private long totalProperties;
    private long totalContracts;
    private long totalUsers;
    private double totalIncome;
    private long pendingPayments;
    private long activeContracts;
    private long availableProperties;
    private long overduePayments;
}
