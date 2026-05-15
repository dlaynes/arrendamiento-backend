package com.grupo2is2.arrendamiento.dto;

import com.grupo2is2.arrendamiento.domain.DocumentEntityType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class DocumentDto {
    private Long id;
    private String originalName;
    private Long fileSize;
    private String contentType;
    private DocumentEntityType entityType;
    private Long entityId;
    private String uploadedByName;
    private LocalDateTime createdAt;
}
