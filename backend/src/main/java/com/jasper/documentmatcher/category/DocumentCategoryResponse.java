package com.jasper.documentmatcher.category;

import java.util.UUID;

public record DocumentCategoryResponse(UUID id, String code, String displayName, CategoryOrigin origin) {

    static DocumentCategoryResponse from(DocumentCategory category) {
        return new DocumentCategoryResponse(
                category.getId(), category.getCode(), category.getDisplayName(), category.getOrigin());
    }
}
