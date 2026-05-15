package com.grupo2is2.arrendamiento.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {
    ADMINISTRADOR,
    ARRENDADOR,
    INQUILINO;

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
