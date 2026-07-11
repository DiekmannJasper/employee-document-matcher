package com.jasper.documentmatcher.document;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentAnalysisRepository extends JpaRepository<DocumentAnalysis, UUID> {

    Optional<DocumentAnalysis> findByDocumentId(UUID documentId);

    List<DocumentAnalysis> findByDocumentIdIn(Collection<UUID> documentIds);
}
