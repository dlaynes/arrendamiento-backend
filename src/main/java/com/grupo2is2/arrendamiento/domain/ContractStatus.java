package com.grupo2is2.arrendamiento.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ContractStatus {
    ACTIVO,
    PROXIMO_VENCER,
    VENCIDO,
    CANCELADO,
    TERMINADO;

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
