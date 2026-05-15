package com.grupo2is2.arrendamiento.repository;

import com.grupo2is2.arrendamiento.domain.Document;
import com.grupo2is2.arrendamiento.domain.DocumentEntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(DocumentEntityType entityType, Long entityId);
}
