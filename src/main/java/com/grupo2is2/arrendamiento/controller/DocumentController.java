package com.grupo2is2.arrendamiento.controller;

import com.grupo2is2.arrendamiento.domain.DocumentEntityType;
import com.grupo2is2.arrendamiento.domain.User;
import com.grupo2is2.arrendamiento.dto.DocumentDto;
import com.grupo2is2.arrendamiento.repository.DocumentRepository;
import com.grupo2is2.arrendamiento.repository.UserRepository;
import com.grupo2is2.arrendamiento.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class DocumentController {

    private final DocumentService documentService;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentDto> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") DocumentEntityType type,
            @RequestParam("entityId") Long entityId) {
        return ResponseEntity.ok(documentService.upload(file, type, entityId, getCurrentUserId()));
    }

    @GetMapping
    public ResponseEntity<List<DocumentDto>> getDocuments(
            @RequestParam("type") DocumentEntityType type,
            @RequestParam("entityId") Long entityId) {
        return ResponseEntity.ok(documentService.getDocuments(type, entityId));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Resource resource = documentService.download(id);
        String originalName = documentRepository.findById(id)
                .map(d -> d.getOriginalName())
                .orElse("documento");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalName + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        documentService.delete(id, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}
