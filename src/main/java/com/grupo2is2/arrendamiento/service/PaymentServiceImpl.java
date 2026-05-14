package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.domain.Contract;
import com.grupo2is2.arrendamiento.domain.Payment;
import com.grupo2is2.arrendamiento.domain.PaymentStatus;
import com.grupo2is2.arrendamiento.domain.User;
import com.grupo2is2.arrendamiento.dto.PaymentDto;
import com.grupo2is2.arrendamiento.repository.ContractRepository;
import com.grupo2is2.arrendamiento.repository.PaymentRepository;
import com.grupo2is2.arrendamiento.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;

    @Override
    public List<PaymentDto> getAll() {
        return paymentRepository.findAllWithUser().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDto getById(Long id) {
        Payment payment = paymentRepository.findByIdWithUser(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        return toDto(payment);
    }

    @Override
    public List<PaymentDto> getByContract(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));
        return paymentRepository.findByContract(contract).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto> getByTenant(Long tenantId) {
        return paymentRepository.findByTenantIdWithUser(tenantId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto> getPending() {
        return paymentRepository.findByStatus(PaymentStatus.PENDIENTE).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDto create(PaymentDto dto) {
        Contract contract = contractRepository.findById(dto.getContractId())
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));

        User tenant = null;
        if (dto.getTenantId() != null) {
            tenant = userRepository.findById(dto.getTenantId())
                    .orElseThrow(() -> new RuntimeException("Inquilino no encontrado"));
        }

        Payment payment = Payment.builder()
                .contract(contract)
                .tenant(tenant)
                .property(dto.getProperty())
                .propertyAddress(dto.getPropertyAddress())
                .amount(dto.getAmount())
                .status(dto.getStatus())
                .method(dto.getMethod())
                .dueDate(dto.getDueDate())
                .paidDate(dto.getPaidDate())
                .referenceNumber(dto.getReferenceNumber())
                .transactionId(dto.getTransactionId())
                .notes(dto.getNotes())
                .build();

        Payment saved = paymentRepository.save(payment);
        return toDto(saved);
    }

    @Override
    public PaymentDto update(Long id, PaymentDto dto) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        payment.setProperty(dto.getProperty());
        payment.setPropertyAddress(dto.getPropertyAddress());
        payment.setAmount(dto.getAmount());
        payment.setStatus(dto.getStatus());
        payment.setMethod(dto.getMethod());
        payment.setDueDate(dto.getDueDate());
        payment.setPaidDate(dto.getPaidDate());
        payment.setReferenceNumber(dto.getReferenceNumber());
        payment.setTransactionId(dto.getTransactionId());
        payment.setNotes(dto.getNotes());

        if (dto.getContractId() != null) {
            Contract contract = contractRepository.findById(dto.getContractId())
                    .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));
            payment.setContract(contract);
        }

        if (dto.getTenantId() != null) {
            User tenant = userRepository.findById(dto.getTenantId())
                    .orElseThrow(() -> new RuntimeException("Inquilino no encontrado"));
            payment.setTenant(tenant);
        } else {
            payment.setTenant(null);
        }

        Payment updated = paymentRepository.save(payment);
        return toDto(updated);
    }

    @Override
    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }

    private PaymentDto toDto(Payment payment) {
        User tenant = payment.getTenant();

        return PaymentDto.builder()
                .id(payment.getId())
                .contractId(payment.getContract() != null ? payment.getContract().getId() : null)
                .tenantId(tenant != null ? tenant.getId() : null)
                .tenantName(tenant != null ? tenant.getName() : null)
                .tenantEmail(tenant != null ? tenant.getEmail() : null)
                .property(payment.getProperty())
                .propertyAddress(payment.getPropertyAddress())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .method(payment.getMethod())
                .dueDate(payment.getDueDate())
                .paidDate(payment.getPaidDate())
                .referenceNumber(payment.getReferenceNumber())
                .transactionId(payment.getTransactionId())
                .notes(payment.getNotes())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
