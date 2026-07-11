package com.jasper.documentmatcher.document;

import java.util.UUID;

/**
 * categoryId and newCategoryName are mutually exclusive and both optional - a document can be
 * confirmed to an employee without a category, filed under an existing one, or filed under a new
 * one created on confirmation.
 */
public record ConfirmMatchRequest(UUID employeeId, UUID categoryId, String newCategoryName) {
}
