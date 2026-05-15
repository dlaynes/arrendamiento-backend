package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.domain.DocumentEntityType;
import com.grupo2is2.arrendamiento.dto.DocumentDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface DocumentService {
    DocumentDto upload(MultipartFile file, DocumentEntityType type, Long entityId, Long userId);
    List<DocumentDto> getDocuments(DocumentEntityType type, Long entityId);
    Resource download(Long documentId);
    void delete(Long documentId, Long userId);
}
