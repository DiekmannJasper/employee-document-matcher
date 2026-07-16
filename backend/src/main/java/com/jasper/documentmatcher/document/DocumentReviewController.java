package com.jasper.documentmatcher.document;

import java.util.List;
import java.util.UUID;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents")
public class DocumentReviewController {

    private final DocumentReviewService documentReviewService;

    public DocumentReviewController(DocumentReviewService documentReviewService) {
        this.documentReviewService = documentReviewService;
    }

    @GetMapping("/pending-review")
    public List<PendingReviewResponse> findPendingReviews() {
        return documentReviewService.findPendingReviews();
    }

    @PostMapping("/{documentId}/confirmation")
    public DocumentSummaryResponse confirm(
            @PathVariable UUID documentId, @RequestBody ConfirmMatchRequest request) {
        return documentReviewService.confirm(documentId, request);
    }

    @GetMapping("/{documentId}/file")
    public ResponseEntity<InputStreamResource> openDocument(@PathVariable UUID documentId) {
        var document = documentReviewService.openDocument(documentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.contentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(document.filename()).build().toString())
                .body(new InputStreamResource(document.content()));
    }
}
