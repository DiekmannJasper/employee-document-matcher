package com.jasper.documentmatcher.document;

import static org.assertj.core.api.Assertions.assertThat;

import com.jasper.documentmatcher.AbstractPostgresIntegrationTest;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class DocumentRepositoryTest extends AbstractPostgresIntegrationTest {

    private static final UUID ANNA_MUELLER_ID = UUID.fromString("10000000-0000-0000-0000-000000000001");

    @Autowired private DocumentRepository documentRepository;

    @Test
    void persistsAndReloadsDocumentMetadataWithoutPdfContent() {
        var document = new Document(
                UUID.randomUUID(),
                ANNA_MUELLER_ID,
                "arbeitsvertrag.pdf",
                UUID.randomUUID().toString(),
                "application/pdf",
                DocumentStatus.ASSIGNED,
                Instant.now());

        var saved = documentRepository.save(document);
        var reloaded = documentRepository.findById(saved.getId()).orElseThrow();

        assertThat(reloaded.getOriginalFilename()).isEqualTo("arbeitsvertrag.pdf");
        assertThat(reloaded.getEmployeeId()).isEqualTo(ANNA_MUELLER_ID);
        assertThat(reloaded.getStatus()).isEqualTo(DocumentStatus.ASSIGNED);
    }

    @Test
    void findsDocumentsByEmployeeId() {
        documentRepository.save(new Document(
                UUID.randomUUID(),
                ANNA_MUELLER_ID,
                "lohnabrechnung.pdf",
                UUID.randomUUID().toString(),
                "application/pdf",
                DocumentStatus.ASSIGNED,
                Instant.now()));

        var documents = documentRepository.findByEmployeeId(ANNA_MUELLER_ID);

        assertThat(documents).hasSize(1);
    }

    @Test
    void allowsUnassignedDocumentsWithoutEmployee() {
        var document = new Document(
                UUID.randomUUID(),
                null,
                "unbekannt.pdf",
                UUID.randomUUID().toString(),
                "application/pdf",
                DocumentStatus.UPLOADED,
                Instant.now());

        var saved = documentRepository.save(document);

        assertThat(documentRepository.findById(saved.getId())).isPresent();
    }
}
