package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.domain.Contract;
import com.grupo2is2.arrendamiento.domain.Property;
import com.grupo2is2.arrendamiento.dto.ContractDto;
import com.grupo2is2.arrendamiento.repository.ContractRepository;
import com.grupo2is2.arrendamiento.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final PropertyRepository propertyRepository;

    @Override
    public List<ContractDto> getAll() {
        return contractRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ContractDto getById(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));
        return toDto(contract);
    }

    @Override
    public List<ContractDto> getByOwner(Long ownerId) {
        return contractRepository.findByPropertyOwnerId(ownerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractDto> getByTenant(String tenantName) {
        return contractRepository.findByTenant(tenantName).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractDto> getByProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
        return contractRepository.findByProperty(property).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ContractDto create(ContractDto dto) {
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));

        Contract contract = Contract.builder()
                .code(dto.getCode())
                .tenant(dto.getTenant())
                .tenantEmail(dto.getTenantEmail())
                .tenantPhone(dto.getTenantPhone())
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
        contract.setTenant(dto.getTenant());
        contract.setTenantEmail(dto.getTenantEmail());
        contract.setTenantPhone(dto.getTenantPhone());
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

        Contract updated = contractRepository.save(contract);
        return toDto(updated);
    }

    @Override
    public void delete(Long id) {
        contractRepository.deleteById(id);
    }

    private ContractDto toDto(Contract contract) {
        return ContractDto.builder()
                .id(contract.getId())
                .code(contract.getCode())
                .tenant(contract.getTenant())
                .tenantEmail(contract.getTenantEmail())
                .tenantPhone(contract.getTenantPhone())
                .propertyId(contract.getProperty() != null ? contract.getProperty().getId() : null)
                .property(contract.getProperty() != null ? contract.getProperty().getName() : null)
                .propertyAddress(contract.getProperty() != null ? contract.getProperty().getAddress() : null)
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
