package com.grupo2is2.arrendamiento.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentStatus {
    PENDIENTE,
    PROCESANDO,
    PAGADO,
    VENCIDO,
    RECHAZADO;

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
