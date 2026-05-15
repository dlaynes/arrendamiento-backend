package com.grupo2is2.arrendamiento.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {
    TRANSFERENCIA,
    CHEQUE,
    TARJETA,
    EFECTIVO,
    DIGITAL;

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
