package com.grupo2is2.arrendamiento.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PropertyStatus {
    DISPONIBLE,
    OCUPADO,
    MANTENIMIENTO;

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
