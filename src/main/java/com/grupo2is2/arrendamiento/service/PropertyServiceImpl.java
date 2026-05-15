package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.domain.Amenity;
import com.grupo2is2.arrendamiento.domain.Contract;
import com.grupo2is2.arrendamiento.domain.ContractStatus;
import com.grupo2is2.arrendamiento.domain.Property;
import com.grupo2is2.arrendamiento.domain.PropertyStatus;
import com.grupo2is2.arrendamiento.domain.User;
import com.grupo2is2.arrendamiento.dto.PropertyDto;
import com.grupo2is2.arrendamiento.repository.AmenityRepository;
import com.grupo2is2.arrendamiento.repository.ContractRepository;
import com.grupo2is2.arrendamiento.repository.PropertyRepository;
import com.grupo2is2.arrendamiento.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;
    private final ContractRepository contractRepository;

    @Override
    public List<PropertyDto> getAll() {
        return propertyRepository.findAllWithOwner().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PropertyDto getById(Long id) {
        Property property = propertyRepository.findByIdWithOwner(id)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
        return toDto(property);
    }

    @Override
    public PropertyDto getById(Long id, Long currentUserId) {
        Property property = propertyRepository.findByIdWithOwner(id)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
        if (!property.getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedException("No tienes permiso para ver esta propiedad");
        }
        return toDto(property);
    }

    @Override
    public List<PropertyDto> getByOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));
        return propertyRepository.findByOwner(owner).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyDto> getAvailable() {
        return propertyRepository.findByStatus(PropertyStatus.DISPONIBLE).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PropertyDto create(PropertyDto dto) {
        User owner = userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        List<Amenity> amenities = resolveAmenities(dto.getAmenities());

        Property property = Property.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .type(dto.getType())
                .bedrooms(dto.getBedrooms())
                .bathrooms(dto.getBathrooms())
                .area(dto.getArea())
                .rent(dto.getRent())
                .status(dto.getStatus())
                .description(dto.getDescription())
                .yearBuilt(dto.getYearBuilt())
                .floors(dto.getFloors())
                .furnished(dto.getFurnished())
                .amenities(amenities)
                .owner(owner)
                .build();

        Property saved = propertyRepository.save(property);
        return toDto(saved);
    }

    @Override
    public PropertyDto update(Long id, PropertyDto dto) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));

        property.setName(dto.getName());
        property.setAddress(dto.getAddress());
        property.setType(dto.getType());
        property.setBedrooms(dto.getBedrooms());
        property.setBathrooms(dto.getBathrooms());
        property.setArea(dto.getArea());
        property.setRent(dto.getRent());
        property.setStatus(dto.getStatus());
        property.setDescription(dto.getDescription());
        property.setYearBuilt(dto.getYearBuilt());
        property.setFloors(dto.getFloors());
        property.setFurnished(dto.getFurnished());
        property.setAmenities(resolveAmenities(dto.getAmenities()));

        if (dto.getOwnerId() != null) {
            User owner = userRepository.findById(dto.getOwnerId())
                    .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));
            property.setOwner(owner);
        }

        Property updated = propertyRepository.save(property);
        return toDto(updated);
    }

    @Override
    public PropertyDto update(Long id, PropertyDto dto, Long currentUserId) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
        if (!property.getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedException("No tienes permiso para modificar esta propiedad");
        }
        dto.setOwnerId(currentUserId);
        return update(id, dto);
    }

    @Override
    public void delete(Long id) {
        propertyRepository.deleteById(id);
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
        if (!property.getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedException("No tienes permiso para eliminar esta propiedad");
        }
        propertyRepository.deleteById(id);
    }

    private List<Amenity> resolveAmenities(List<String> names) {
        if (names == null || names.isEmpty()) {
            return List.of();
        }
        return names.stream()
                .map(name -> amenityRepository.findByName(name)
                        .orElseThrow(() -> new RuntimeException("Amenity no encontrada: " + name)))
                .collect(Collectors.toList());
    }

    private PropertyDto toDto(Property property) {
        User owner = property.getOwner();

        List<Contract> contracts = contractRepository.findByPropertyIdWithUsers(property.getId());
        Contract activeContract = contracts.stream()
                .filter(c -> c.getStatus() == ContractStatus.ACTIVO)
                .findFirst()
                .orElse(null);

        User tenant = activeContract != null ? activeContract.getTenant() : null;

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
                .tenantId(tenant != null ? tenant.getId() : null)
                .tenantName(tenant != null ? tenant.getName() : null)
                .createdAt(property.getCreatedAt())
                .updatedAt(property.getUpdatedAt())
                .build();
    }
}
