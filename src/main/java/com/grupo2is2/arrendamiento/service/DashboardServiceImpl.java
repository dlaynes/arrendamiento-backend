package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.domain.*;
import com.grupo2is2.arrendamiento.dto.ContractDto;
import com.grupo2is2.arrendamiento.dto.DashboardStatsDto;
import com.grupo2is2.arrendamiento.dto.PaymentDto;
import com.grupo2is2.arrendamiento.dto.PropertyDto;
import com.grupo2is2.arrendamiento.repository.ContractRepository;
import com.grupo2is2.arrendamiento.repository.PaymentRepository;
import com.grupo2is2.arrendamiento.repository.PropertyRepository;
import com.grupo2is2.arrendamiento.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final PropertyRepository propertyRepository;
    private final ContractRepository contractRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    public DashboardStatsDto getStats() {
        User user = getCurrentUser();
        UserRole role = user.getRole();

        List<Property> properties;
        List<Contract> contracts;
        List<Payment> payments;

        if (role == UserRole.ADMINISTRADOR) {
            properties = propertyRepository.findAll();
            contracts = contractRepository.findAllWithUsers();
            payments = paymentRepository.findAllWithUser();
        } else if (role == UserRole.ARRENDADOR) {
            properties = propertyRepository.findByOwner(user);
            contracts = contractRepository.findByPropertyOwnerIdWithUsers(user.getId());
            payments = paymentRepository.findByContractPropertyOwnerId(user.getId());
        } else { // INQUILINO
            properties = List.of();
            contracts = contractRepository.findByTenantIdWithUsers(user.getId());
            payments = paymentRepository.findByTenantIdWithUser(user.getId());
        }

        long totalProperties = properties.size();
        long totalContracts = contracts.size();
        long totalUsers = (role == UserRole.ADMINISTRADOR) ? userRepository.count() : 0L;
        double totalIncome = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAGADO)
                .mapToDouble(p -> parseAmount(p.getAmount()))
                .sum();
        long pendingPayments = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDIENTE)
                .count();
        long activeContracts = contracts.stream()
                .filter(c -> c.getStatus() == ContractStatus.ACTIVO)
                .count();
        long availableProperties = properties.stream()
                .filter(p -> p.getStatus() == PropertyStatus.DISPONIBLE)
                .count();
        long overduePayments = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.VENCIDO ||
                        (p.getStatus() == PaymentStatus.PENDIENTE && p.getDueDate() != null && p.getDueDate().isBefore(LocalDate.now())))
                .count();

        return DashboardStatsDto.builder()
                .totalProperties(totalProperties)
                .totalContracts(totalContracts)
                .totalUsers(totalUsers)
                .totalIncome(totalIncome)
                .pendingPayments(pendingPayments)
                .activeContracts(activeContracts)
                .availableProperties(availableProperties)
                .overduePayments(overduePayments)
                .build();
    }

    @Override
    public List<PropertyDto> getMyProperties() {
        User user = getCurrentUser();
        List<Property> properties;

        if (user.getRole() == UserRole.ADMINISTRADOR) {
            properties = propertyRepository.findAllWithOwner();
        } else if (user.getRole() == UserRole.ARRENDADOR) {
            properties = propertyRepository.findByOwner(user);
        } else {
            properties = List.of();
        }

        return properties.stream()
                .map(this::toPropertyDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractDto> getMyContracts() {
        User user = getCurrentUser();
        UserRole role = user.getRole();
        List<Contract> contracts;

        if (role == UserRole.ADMINISTRADOR) {
            contracts = contractRepository.findAllWithUsers();
        } else if (role == UserRole.ARRENDADOR) {
            contracts = contractRepository.findByPropertyOwnerIdWithUsers(user.getId());
        } else {
            contracts = contractRepository.findByTenantIdWithUsers(user.getId());
        }

        return contracts.stream()
                .map(this::toContractDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto> getMyPayments() {
        User user = getCurrentUser();
        UserRole role = user.getRole();
        List<Payment> payments;

        if (role == UserRole.ADMINISTRADOR) {
            payments = paymentRepository.findAllWithUser();
        } else if (role == UserRole.ARRENDADOR) {
            payments = paymentRepository.findByContractPropertyOwnerId(user.getId());
        } else {
            payments = paymentRepository.findByTenantIdWithUser(user.getId());
        }

        return payments.stream()
                .map(this::toPaymentDto)
                .collect(Collectors.toList());
    }

    private double parseAmount(String amount) {
        if (amount == null || amount.isBlank()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(amount.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private PropertyDto toPropertyDto(Property property) {
        User owner = property.getOwner();

        return PropertyDto.builder()
                .id(property.getId())
                .name(property.getName())
                .address(property.getAddress())
                .type(property.getType())
                .bedrooms(property.getBedrooms())
                .bathrooms(property.getBathrooms())
                .area(property.getArea())
                .rent(property.getRent())
                .status(property.getStatus())
                .description(property.getDescription())
                .yearBuilt(property.getYearBuilt())
                .floors(property.getFloors())
                .furnished(property.getFurnished())
                .amenities(property.getAmenities().stream()
                        .map(Amenity::getName)
                        .collect(Collectors.toList()))
                .ownerId(owner != null ? owner.getId() : null)
                .createdAt(property.getCreatedAt())
                .updatedAt(property.getUpdatedAt())
                .build();
    }

    private ContractDto toContractDto(Contract contract) {
        User tenant = contract.getTenant();
        User landlord = contract.getLandlord();
        Property property = contract.getProperty();

        return ContractDto.builder()
                .id(contract.getId())
                .code(contract.getCode())
                .tenantId(tenant != null ? tenant.getId() : null)
                .tenantName(tenant != null ? tenant.getName() : null)
                .tenantEmail(tenant != null ? tenant.getEmail() : null)
                .landlordId(landlord != null ? landlord.getId() : null)
                .landlordName(landlord != null ? landlord.getName() : null)
                .landlordEmail(landlord != null ? landlord.getEmail() : null)
                .propertyId(property != null ? property.getId() : null)
                .property(property != null ? property.getName() : null)
                .propertyAddress(property != null ? property.getAddress() : null)
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .monthlyRent(contract.getMonthlyRent())
                .deposit(contract.getDeposit())
                .status(contract.getStatus())
                .paymentDay(contract.getPaymentDay())
                .terms(contract.getTerms())
                .notes(contract.getNotes())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .build();
    }

    private PaymentDto toPaymentDto(Payment payment) {
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
