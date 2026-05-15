package com.grupo2is2.arrendamiento.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PropertyType {
    APARTAMENTO,
    CASA,
    ESTUDIO,
    LOFT,
    PENTHOUSE,
    VILLA,
    OTRO;

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
