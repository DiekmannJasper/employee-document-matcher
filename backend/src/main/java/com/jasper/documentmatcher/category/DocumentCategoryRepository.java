package com.jasper.documentmatcher.category;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentCategoryRepository extends JpaRepository<DocumentCategory, UUID> {

    Optional<DocumentCategory> findByCode(String code);
}
