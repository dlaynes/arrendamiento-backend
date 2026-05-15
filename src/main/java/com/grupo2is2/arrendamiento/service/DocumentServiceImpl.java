package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.domain.Document;
import com.grupo2is2.arrendamiento.domain.DocumentEntityType;
import com.grupo2is2.arrendamiento.domain.User;
import com.grupo2is2.arrendamiento.domain.UserRole;
import com.grupo2is2.arrendamiento.dto.DocumentDto;
import com.grupo2is2.arrendamiento.repository.DocumentRepository;
import com.grupo2is2.arrendamiento.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 4 * 1024 * 1024; // 4MB

    private static final Set<String> ALLOWED_TYPES = Set.of(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/webp",
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/plain"
    );

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de uploads: " + uploadDir, e);
        }
    }

    @Override
    public DocumentDto upload(MultipartFile file, DocumentEntityType type, Long entityId, Long userId) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("El archivo excede el límite de 4MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new RuntimeException("Tipo de archivo no permitido. Solo se permiten imágenes, PDF, Word, Excel y archivos de texto.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            originalName = "archivo";
        }

        String fileName = UUID.randomUUID() + "_" + originalName;
        Path targetDir = Paths.get(uploadDir, type.name().toLowerCase(), String.valueOf(entityId));
        Path targetPath = targetDir.resolve(fileName);

        try {
            Files.createDirectories(targetDir);
            file.transferTo(targetPath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo", e);
        }

        Document doc = Document.builder()
                .fileName(fileName)
                .originalName(originalName)
                .fileSize(file.getSize())
                .contentType(contentType)
                .entityType(type)
                .entityId(entityId)
                .uploadedBy(user)
                .build();

        Document saved = documentRepository.save(doc);
        return toDto(saved);
    }

    @Override
    public List<DocumentDto> getDocuments(DocumentEntityType type, Long entityId) {
        return documentRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(type, entityId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Resource download(Long documentId) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        Path filePath = Paths.get(uploadDir,
                doc.getEntityType().name().toLowerCase(),
                String.valueOf(doc.getEntityId()),
                doc.getFileName());

        if (!Files.exists(filePath)) {
            throw new RuntimeException("Archivo no encontrado en disco");
        }

        return new FileSystemResource(filePath.toFile());
    }

    @Override
    public void delete(Long documentId, Long userId) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean canDelete = doc.getUploadedBy().getId().equals(userId) || user.getRole() == UserRole.ADMINISTRADOR;
        if (!canDelete) {
            throw new RuntimeException("No tienes permiso para eliminar este documento");
        }

        Path filePath = Paths.get(uploadDir,
                doc.getEntityType().name().toLowerCase(),
                String.valueOf(doc.getEntityId()),
                doc.getFileName());

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar el archivo", e);
        }

        documentRepository.delete(doc);
    }

    private DocumentDto toDto(Document doc) {
        return DocumentDto.builder()
                .id(doc.getId())
                .originalName(doc.getOriginalName())
                .fileSize(doc.getFileSize())
                .contentType(doc.getContentType())
                .entityType(doc.getEntityType())
                .entityId(doc.getEntityId())
                .uploadedByName(doc.getUploadedBy() != null ? doc.getUploadedBy().getName() : null)
                .createdAt(doc.getCreatedAt())
                .build();
    }
}
