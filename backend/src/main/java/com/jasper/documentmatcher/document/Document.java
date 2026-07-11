package com.jasper.documentmatcher.document;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    private UUID id;

    @Column(name = "employee_id")
    private UUID employeeId;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "storage_key", nullable = false, unique = true)
    private String storageKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DocumentStatus status;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private Instant uploadedAt;

    protected Document() {
    }

    Document(
            UUID id,
            UUID employeeId,
            String originalFilename,
            String storageKey,
            DocumentStatus status,
            Instant uploadedAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.originalFilename = originalFilename;
        this.storageKey = storageKey;
        this.status = status;
        this.uploadedAt = uploadedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    void assignToEmployee(UUID employeeId) {
        this.employeeId = employeeId;
        this.status = DocumentStatus.ASSIGNED;
    }
}
