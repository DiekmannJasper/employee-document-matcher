package com.jasper.documentmatcher.document;

import com.jasper.documentmatcher.category.CategoryOrigin;
import com.jasper.documentmatcher.category.DocumentCategoryService;
import com.jasper.documentmatcher.confidence.ConfidenceBandCalculator;
import com.jasper.documentmatcher.employee.EmployeeService;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class DocumentReviewService {

    private final EmployeeService employeeService;
    private final DocumentCategoryService documentCategoryService;
    private final ConfidenceBandCalculator confidenceBandCalculator;
    private final DocumentRepository documentRepository;
    private final DocumentAnalysisRepository documentAnalysisRepository;

    DocumentReviewService(
            EmployeeService employeeService,
            DocumentCategoryService documentCategoryService,
            ConfidenceBandCalculator confidenceBandCalculator,
            DocumentRepository documentRepository,
            DocumentAnalysisRepository documentAnalysisRepository) {
        this.employeeService = employeeService;
        this.documentCategoryService = documentCategoryService;
        this.confidenceBandCalculator = confidenceBandCalculator;
        this.documentRepository = documentRepository;
        this.documentAnalysisRepository = documentAnalysisRepository;
    }

    List<PendingReviewResponse> findPendingReviews() {
        return documentAnalysisRepository.findByReviewStatus(ReviewStatus.PENDING).stream()
                .map(analysis -> PendingReviewResponse.from(
                        documentRepository
                                .findById(analysis.getDocumentId())
                                .orElseThrow(() -> new DocumentNotFoundException(analysis.getDocumentId())),
                        analysis,
                        confidenceBandCalculator))
                .toList();
    }

    @Transactional
    DocumentSummaryResponse confirm(UUID documentId, ConfirmMatchRequest request) {
        if (request == null || request.employeeId() == null) {
            throw new InvalidReviewRequestException("employeeId ist erforderlich.");
        }
        if (request.categoryId() != null && request.newCategoryName() != null) {
            throw new InvalidReviewRequestException("categoryId und newCategoryName schließen sich aus.");
        }

        employeeService.findById(request.employeeId());

        var document =
                documentRepository.findById(documentId).orElseThrow(() -> new DocumentNotFoundException(documentId));
        var analysis = documentAnalysisRepository
                .findByDocumentId(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));

        if (analysis.getReviewStatus() != ReviewStatus.PENDING) {
            throw new DocumentAlreadyReviewedException(documentId);
        }

        document.assignToEmployee(request.employeeId());
        resolveCategory(request, analysis).ifPresent(document::assignCategory);
        analysis.confirm();

        return DocumentSummaryResponse.from(document);
    }

    private Optional<UUID> resolveCategory(ConfirmMatchRequest request, DocumentAnalysis analysis) {
        if (request.categoryId() != null) {
            documentCategoryService.findById(request.categoryId());
            return Optional.of(request.categoryId());
        }

        if (request.newCategoryName() != null && !request.newCategoryName().isBlank()) {
            var origin = isSuggestedName(request.newCategoryName(), analysis.getSuggestedCategoryName())
                    ? CategoryOrigin.LLM_SUGGESTED
                    : CategoryOrigin.MANUAL;
            return Optional.of(documentCategoryService.resolveOrCreateByDisplayName(request.newCategoryName(), origin));
        }

        return Optional.empty();
    }

    private boolean isSuggestedName(String confirmedName, String suggestedName) {
        return suggestedName != null
                && suggestedName.trim().toLowerCase(Locale.ROOT).equals(confirmedName.trim().toLowerCase(Locale.ROOT));
    }
}
