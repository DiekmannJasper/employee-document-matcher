package com.jasper.documentmatcher.category;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/document-categories")
public class DocumentCategoryController {

    private final DocumentCategoryService documentCategoryService;

    public DocumentCategoryController(DocumentCategoryService documentCategoryService) {
        this.documentCategoryService = documentCategoryService;
    }

    @GetMapping
    public List<DocumentCategoryResponse> findAll() {
        return documentCategoryService.findAll();
    }
}
