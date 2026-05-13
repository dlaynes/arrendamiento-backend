package com.grupo2is2.arrendamiento.dto;

import com.grupo2is2.arrendamiento.domain.UserRole;
import com.grupo2is2.arrendamiento.domain.UserStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private UserStatus status;
    private String avatar;
    private LocalDateTime lastLogin;
    private List<Long> propertyIds;
}