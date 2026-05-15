package com.grupo2is2.arrendamiento.security;

import com.grupo2is2.arrendamiento.domain.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

public enum UserAuthority {
    PROPERTY_READ,
    PROPERTY_WRITE,
    CONTRACT_READ,
    CONTRACT_WRITE,
    PAYMENT_READ,
    PAYMENT_WRITE,
    USER_READ,
    USER_WRITE,
    REPORT_READ,
    REPORT_WRITE,
    DASHBOARD_READ,
    MESSAGE_READ,
    MESSAGE_WRITE,
    TENANT_READ;

    private static final Set<UserAuthority> ADMIN_AUTHORITIES = Set.of(values());

    private static final Set<UserAuthority> LANDLORD_AUTHORITIES = Set.of(
            PROPERTY_READ, PROPERTY_WRITE,
            CONTRACT_READ, CONTRACT_WRITE,
            PAYMENT_READ,
            TENANT_READ,
            DASHBOARD_READ,
            MESSAGE_READ, MESSAGE_WRITE
    );

    private static final Set<UserAuthority> TENANT_AUTHORITIES = Set.of(
            PROPERTY_READ,
            CONTRACT_READ,
            PAYMENT_READ, PAYMENT_WRITE,
            DASHBOARD_READ,
            MESSAGE_READ, MESSAGE_WRITE
    );

    public static List<? extends GrantedAuthority> fromRole(UserRole role) {
        Set<UserAuthority> authorities = switch (role) {
            case ADMINISTRADOR -> ADMIN_AUTHORITIES;
            case ARRENDADOR -> LANDLORD_AUTHORITIES;
            case INQUILINO -> TENANT_AUTHORITIES;
        };
        return authorities.stream()
                .map(a -> new SimpleGrantedAuthority(a.name()))
                .toList();
    }
}
