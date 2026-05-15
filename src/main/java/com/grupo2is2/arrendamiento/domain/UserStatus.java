package com.grupo2is2.arrendamiento.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus {
    ACTIVO,
    INACTIVO,
    SUSPENDIDO;

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
