package com.jasper.documentmatcher.category;

import java.text.Normalizer;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DocumentCategoryService {

    private final DocumentCategoryRepository documentCategoryRepository;

    public DocumentCategoryService(DocumentCategoryRepository documentCategoryRepository) {
        this.documentCategoryRepository = documentCategoryRepository;
    }

    public List<DocumentCategoryResponse> findAll() {
        return documentCategoryRepository.findAll().stream().map(DocumentCategoryResponse::from).toList();
    }

    public DocumentCategoryResponse findById(UUID categoryId) {
        return documentCategoryRepository
                .findById(categoryId)
                .map(DocumentCategoryResponse::from)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    /**
     * Reuses an existing category if its display name normalizes to the same value, preventing
     * near-duplicate folders (e.g. "Kündigungen" vs "kuendigungen ").
     */
    public UUID resolveOrCreateByDisplayName(String displayName, CategoryOrigin origin) {
        var normalizedTarget = normalize(displayName);

        return documentCategoryRepository.findAll().stream()
                .filter(category -> normalize(category.getDisplayName()).equals(normalizedTarget))
                .findFirst()
                .map(DocumentCategory::getId)
                .orElseGet(() -> createCategory(displayName, origin));
    }

    private UUID createCategory(String displayName, CategoryOrigin origin) {
        var category = new DocumentCategory(UUID.randomUUID(), generateUniqueCode(displayName), displayName, origin, Instant.now());
        documentCategoryRepository.save(category);
        return category.getId();
    }

    private String generateUniqueCode(String displayName) {
        var base = transliterate(displayName)
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
        if (base.isBlank()) {
            base = "CATEGORY";
        }

        var candidate = base;
        var suffix = 2;
        while (documentCategoryRepository.findByCode(candidate).isPresent()) {
            candidate = base + "_" + suffix++;
        }
        return candidate;
    }

    private String transliterate(String value) {
        return value
                .replace("ä", "ae")
                .replace("Ä", "Ae")
                .replace("ö", "oe")
                .replace("Ö", "Oe")
                .replace("ü", "ue")
                .replace("Ü", "Ue")
                .replace("ß", "ss");
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return Normalizer.normalize(value, Normalizer.Form.NFKC)
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("\\s+", " ");
    }
}
