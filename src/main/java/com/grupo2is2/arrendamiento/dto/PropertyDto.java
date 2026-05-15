package com.grupo2is2.arrendamiento.dto;

import com.grupo2is2.arrendamiento.domain.PropertyStatus;
import com.grupo2is2.arrendamiento.domain.PropertyType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PropertyDto {
    private Long id;
    private String name;
    private String address;
    private PropertyType type;
    private Integer bedrooms;
    private Integer bathrooms;
    private String area;
    private String rent;
    private PropertyStatus status;
    private String description;
    private Integer yearBuilt;
    private Integer floors;
    private Boolean furnished;
    private List<String> amenities;
    private Long ownerId;
    private Long tenantId;
    private String tenantName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
