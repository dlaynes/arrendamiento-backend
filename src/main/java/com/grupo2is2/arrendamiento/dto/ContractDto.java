package com.grupo2is2.arrendamiento.dto;

import com.grupo2is2.arrendamiento.domain.ContractStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ContractDto {
    private Long id;
    private String code;
    private Long tenantId;
    private String tenantName;
    private String tenantEmail;
    private Long landlordId;
    private String landlordName;
    private String landlordEmail;
    private Long propertyId;
    private String property;
    private String propertyAddress;
    private LocalDate startDate;
    private LocalDate endDate;
    private String monthlyRent;
    private String deposit;
    private ContractStatus status;
    private Integer paymentDay;
    private List<String> terms;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
