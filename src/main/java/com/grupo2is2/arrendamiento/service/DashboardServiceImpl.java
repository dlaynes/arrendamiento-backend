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

    @Override
    public DashboardStatsDto getStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Property> myProperties = propertyRepository.findByOwner(user);
        List<Contract> myContracts = contractRepository.findByPropertyOwnerIdWithUsers(userId);
        List<Payment> myPayments = myContracts.stream()
                .flatMap(c -> paymentRepository.findByContract(c).stream())
                .toList();

        long totalProperties = myProperties.size();
        long totalContracts = myContracts.size();
        long totalUsers = userRepository.count();
        double totalIncome = myPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAGADO)
                .mapToDouble(p -> parseAmount(p.getAmount()))
                .sum();
        long pendingPayments = myPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDIENTE)
                .count();
        long activeContracts = myContracts.stream()
                .filter(c -> c.getStatus() == ContractStatus.ACTIVO)
                .count();
        long availableProperties = myProperties.stream()
                .filter(p -> p.getStatus() == PropertyStatus.DISPONIBLE)
                .count();
        long overduePayments = myPayments.stream()
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
    public List<PropertyDto> getMyProperties(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return propertyRepository.findByOwner(user).stream()
                .map(this::toPropertyDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractDto> getMyContracts(Long userId) {
        return contractRepository.findByPropertyOwnerIdWithUsers(userId).stream()
                .map(this::toContractDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto> getMyPayments(Long userId) {
        List<Contract> contracts = contractRepository.findByPropertyOwnerIdWithUsers(userId);
        return contracts.stream()
                .flatMap(c -> paymentRepository.findByContract(c).stream())
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
