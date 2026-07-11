package com.jasper.documentmatcher.document;

import com.jasper.documentmatcher.employee.EmployeeService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class DocumentReviewService {

    private final EmployeeService employeeService;
    private final DocumentRepository documentRepository;
    private final DocumentAnalysisRepository documentAnalysisRepository;

    DocumentReviewService(
            EmployeeService employeeService,
            DocumentRepository documentRepository,
            DocumentAnalysisRepository documentAnalysisRepository) {
        this.employeeService = employeeService;
        this.documentRepository = documentRepository;
        this.documentAnalysisRepository = documentAnalysisRepository;
    }

    List<PendingReviewResponse> findPendingReviews() {
        return documentAnalysisRepository.findByReviewStatus(ReviewStatus.PENDING).stream()
                .map(analysis -> PendingReviewResponse.from(
                        documentRepository
                                .findById(analysis.getDocumentId())
                                .orElseThrow(() -> new DocumentNotFoundException(analysis.getDocumentId())),
                        analysis))
                .toList();
    }

    @Transactional
    DocumentSummaryResponse confirm(UUID documentId, ConfirmMatchRequest request) {
        if (request == null || request.employeeId() == null) {
            throw new InvalidReviewRequestException("employeeId ist erforderlich.");
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
        analysis.confirm();

        return DocumentSummaryResponse.from(document, analysis);
    }
}
