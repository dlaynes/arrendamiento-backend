package com.grupo2is2.arrendamiento.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DocumentEntityType {
    CONTRACT,
    PROPERTY;

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
