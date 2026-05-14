package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.domain.Contract;
import com.grupo2is2.arrendamiento.domain.Property;
import com.grupo2is2.arrendamiento.domain.User;
import com.grupo2is2.arrendamiento.domain.UserRole;
import com.grupo2is2.arrendamiento.dto.ContractDto;
import com.grupo2is2.arrendamiento.repository.ContractRepository;
import com.grupo2is2.arrendamiento.repository.PropertyRepository;
import com.grupo2is2.arrendamiento.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    private String generateInvitationToken() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

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
    public ContractDto getById(Long id, Long currentUserId) {
        Contract contract = contractRepository.findByIdWithUsers(id)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));
        if (!contract.getProperty().getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedException("No tienes permiso para ver este contrato");
        }
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

        User tenant = null;
        if (dto.getTenantId() != null) {
            tenant = userRepository.findById(dto.getTenantId())
                    .orElseThrow(() -> new RuntimeException("Inquilino no encontrado"));
        }

        User landlord = userRepository.findById(dto.getLandlordId())
                .orElseThrow(() -> new RuntimeException("Arrendador no encontrado"));

        String invitationToken = null;
        Long tenantId = dto.getTenantId();
        String newInvitedTenantEmail = dto.getInvitedTenantEmail();
        if (tenantId == null && newInvitedTenantEmail != null) {
            invitationToken = generateInvitationToken();
        }

        Contract contract = Contract.builder()
                .code(dto.getCode())
                .tenant(tenant)
                .landlord(landlord)
                .property(property)
                .invitedTenantName(dto.getInvitedTenantName())
                .invitedTenantEmail(newInvitedTenantEmail)
                .invitedTenantPhone(dto.getInvitedTenantPhone())
                .invitationToken(invitationToken)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .monthlyRent(dto.getMonthlyRent())
                .deposit(dto.getDeposit())
                .status(dto.getStatus())
                .paymentDay(dto.getPaymentDay())
                .terms(dto.getTerms())
                .notes(dto.getNotes())
                .build();

        Contract saved = contractRepository.save(contract);
        return toDto(saved);
    }

    @Override
    public ContractDto update(Long id, ContractDto dto) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));

        if (contract.getTenant() != null) {
            Long tenantId = dto.getTenantId();
            if (tenantId != null) {
                User tenant = userRepository.findById(tenantId)
                        .orElseThrow(() -> new RuntimeException("Inquilino no encontrado"));
                if (tenant.getRole() != UserRole.INQUILINO) {
                    throw new RuntimeException("Inquilino no encontrado");
                }
                contract.setTenant(tenant);
            }
            contract.setInvitedTenantName(dto.getInvitedTenantName());
            contract.setInvitedTenantPhone(dto.getInvitedTenantPhone());

            String prevInvitedTenantEmail = contract.getInvitedTenantEmail();
            String newInvitedTenantEmail = dto.getInvitedTenantEmail();
            if (tenantId == null && newInvitedTenantEmail != null && !Objects.equals(prevInvitedTenantEmail, newInvitedTenantEmail)) {
                contract.setInvitedTenantEmail(dto.getInvitedTenantEmail());
                contract.setInvitationToken(generateInvitationToken());
            }
        }

        contract.setCode(dto.getCode());
        contract.setStartDate(dto.getStartDate());
        contract.setEndDate(dto.getEndDate());
        contract.setMonthlyRent(dto.getMonthlyRent());
        contract.setDeposit(dto.getDeposit());
        contract.setStatus(dto.getStatus());
        contract.setPaymentDay(dto.getPaymentDay());
        contract.setTerms(dto.getTerms());
        contract.setNotes(dto.getNotes());

        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
        contract.setProperty(property);

        // Only allow setting tenant if not already assigned
        if (contract.getTenant() == null && dto.getTenantId() != null) {
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
    public ContractDto update(Long id, ContractDto dto, Long currentUserId) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));
        if (!contract.getProperty().getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedException("No tienes permiso para modificar este contrato");
        }
        return update(id, dto);
    }

    @Override
    public void delete(Long id) {
        contractRepository.deleteById(id);
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));
        if (!contract.getProperty().getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedException("No tienes permiso para eliminar este contrato");
        }
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
                .invitedTenantName(contract.getInvitedTenantName())
                .invitedTenantEmail(contract.getInvitedTenantEmail())
                .invitedTenantPhone(contract.getInvitedTenantPhone())
                .invitationToken(contract.getInvitationToken())
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
