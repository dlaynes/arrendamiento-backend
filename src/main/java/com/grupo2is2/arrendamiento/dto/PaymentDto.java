package com.grupo2is2.arrendamiento.dto;

import com.grupo2is2.arrendamiento.domain.PaymentMethod;
import com.grupo2is2.arrendamiento.domain.PaymentStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentDto {
    private Long id;
    private Long contractId;
    private String tenant;
    private String tenantEmail;
    private String property;
    private String propertyAddress;
    private String amount;
    private PaymentStatus status;
    private PaymentMethod method;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private String referenceNumber;
    private String transactionId;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}