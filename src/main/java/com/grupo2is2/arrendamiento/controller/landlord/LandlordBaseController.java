package com.grupo2is2.arrendamiento.controller.landlord;

import com.grupo2is2.arrendamiento.domain.Property;
import com.grupo2is2.arrendamiento.domain.User;
import com.grupo2is2.arrendamiento.dto.UserDto;
import com.grupo2is2.arrendamiento.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.stream.Collectors;

public abstract class LandlordBaseController {

    @Autowired
    protected UserRepository userRepository;

    protected Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }

    protected UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .avatar(user.getAvatar())
                .lastLogin(user.getLastLogin())
                .propertyIds(user.getProperties().stream()
                        .map(Property::getId)
                        .collect(Collectors.toList()))
                .build();
    }
}
