package com.grupo2is2.arrendamiento.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StartConversationRequest {
    @NotNull
    private Long otherUserId;
}
