package com.jasper.documentmatcher.category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "document_categories")
public class DocumentCategory {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "display_name", nullable = false, length = 150)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoryOrigin origin;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected DocumentCategory() {
    }

    DocumentCategory(UUID id, String code, String displayName, CategoryOrigin origin, Instant createdAt) {
        this.id = id;
        this.code = code;
        this.displayName = displayName;
        this.origin = origin;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public CategoryOrigin getOrigin() {
        return origin;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
