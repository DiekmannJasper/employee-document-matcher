package com.jasper.documentmatcher.category;

import java.util.List;
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
}
