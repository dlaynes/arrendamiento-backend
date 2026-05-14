package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.domain.Contract;
import com.grupo2is2.arrendamiento.domain.Property;
import com.grupo2is2.arrendamiento.domain.User;
import com.grupo2is2.arrendamiento.dto.ContractDto;
import com.grupo2is2.arrendamiento.repository.ContractRepository;
import com.grupo2is2.arrendamiento.repository.PropertyRepository;
import com.grupo2is2.arrendamiento.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @Override
    public List<ContractDto> getAll() {
        return contractRepository.findAllWithUsers().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ContractDto getById(Long id) {
        Contract contract = contractRepository.findByIdWithUsers(id)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));
        return toDto(contract);
    }

    @Override
    public List<ContractDto> getByOwner(Long ownerId) {
        return contractRepository.findByPropertyOwnerIdWithUsers(ownerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractDto> getByTenant(Long tenantId) {
        return contractRepository.findByTenantIdWithUsers(tenantId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractDto> getByLandlord(Long landlordId) {
        return contractRepository.findByLandlordIdWithUsers(landlordId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractDto> getByProperty(Long propertyId) {
        return contractRepository.findByPropertyIdWithUsers(propertyId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ContractDto create(ContractDto dto) {
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));

        User tenant = userRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new RuntimeException("Inquilino no encontrado"));

        User landlord = userRepository.findById(dto.getLandlordId())
                .orElseThrow(() -> new RuntimeException("Arrendador no encontrado"));

        Contract contract = Contract.builder()
                .code(dto.getCode())
                .tenant(tenant)
                .landlord(landlord)
                .property(property)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .monthlyRent(dto.getMonthlyRent())
                .deposit(dto.getDeposit())
                .status(dto.getStatus())
                .paymentDay(dto.getPaymentDay())
                .terms(dto.getTerms() != null ? dto.getTerms() : List.of())
                .notes(dto.getNotes())
                .build();

        Contract saved = contractRepository.save(contract);
        return toDto(saved);
    }

    @Override
    public ContractDto update(Long id, ContractDto dto) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));

        contract.setCode(dto.getCode());
        contract.setStartDate(dto.getStartDate());
        contract.setEndDate(dto.getEndDate());
        contract.setMonthlyRent(dto.getMonthlyRent());
        contract.setDeposit(dto.getDeposit());
        contract.setStatus(dto.getStatus());
        contract.setPaymentDay(dto.getPaymentDay());
        contract.setTerms(dto.getTerms() != null ? dto.getTerms() : List.of());
        contract.setNotes(dto.getNotes());

        if (dto.getPropertyId() != null) {
            Property property = propertyRepository.findById(dto.getPropertyId())
                    .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
            contract.setProperty(property);
        }

        if (dto.getTenantId() != null) {
            User tenant = userRepository.findById(dto.getTenantId())
                    .orElseThrow(() -> new RuntimeException("Inquilino no encontrado"));
            contract.setTenant(tenant);
        }

        if (dto.getLandlordId() != null) {
            User landlord = userRepository.findById(dto.getLandlordId())
                    .orElseThrow(() -> new RuntimeException("Arrendador no encontrado"));
            contract.setLandlord(landlord);
        }

        Contract updated = contractRepository.save(contract);
        return toDto(updated);
    }

    @Override
    public void delete(Long id) {
        contractRepository.deleteById(id);
    }

    private ContractDto toDto(Contract contract) {
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
}
