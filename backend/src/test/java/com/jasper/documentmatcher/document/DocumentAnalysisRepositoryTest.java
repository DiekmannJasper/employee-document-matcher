package com.jasper.documentmatcher.document;

import static org.assertj.core.api.Assertions.assertThat;

import com.jasper.documentmatcher.AbstractPostgresIntegrationTest;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class DocumentAnalysisRepositoryTest extends AbstractPostgresIntegrationTest {

    private static final UUID ANNA_MUELLER_ID = UUID.fromString("10000000-0000-0000-0000-000000000001");
    private static final UUID CONTRACT_CATEGORY_ID = UUID.fromString("20000000-0000-0000-0000-000000000001");

    @Autowired private DocumentRepository documentRepository;
    @Autowired private DocumentAnalysisRepository documentAnalysisRepository;

    @Test
    void persistsAnalysisWithMatchAndCategorySignals() {
        var document = documentRepository.save(new Document(
                UUID.randomUUID(),
                null,
                "arbeitsvertrag.pdf",
                UUID.randomUUID().toString(),
                DocumentStatus.UPLOADED,
                Instant.now()));

        var analysis = new DocumentAnalysis(
                UUID.randomUUID(),
                document.getId(),
                MatchStatus.MATCHED,
                ANNA_MUELLER_ID,
                new BigDecimal("1.0000"),
                CONTRACT_CATEGORY_ID,
                new BigDecimal("0.8500"),
                "Exakter Namenstreffer: 'Anna Müller'",
                ReviewStatus.PENDING,
                Instant.now());

        var saved = documentAnalysisRepository.save(analysis);
        var reloaded = documentAnalysisRepository.findById(saved.getId()).orElseThrow();

        assertThat(reloaded.getMatchStatus()).isEqualTo(MatchStatus.MATCHED);
        assertThat(reloaded.getMatchedEmployeeId()).isEqualTo(ANNA_MUELLER_ID);
        assertThat(reloaded.getSuggestedCategoryId()).isEqualTo(CONTRACT_CATEGORY_ID);
        assertThat(reloaded.getReviewStatus()).isEqualTo(ReviewStatus.PENDING);
        assertThat(reloaded.getEvidence()).contains("Anna Müller");
    }

    @Test
    void findsAnalysisByDocumentId() {
        var document = documentRepository.save(new Document(
                UUID.randomUUID(),
                null,
                "unklar.pdf",
                UUID.randomUUID().toString(),
                DocumentStatus.UPLOADED,
                Instant.now()));

        documentAnalysisRepository.save(new DocumentAnalysis(
                UUID.randomUUID(),
                document.getId(),
                MatchStatus.AMBIGUOUS,
                null,
                null,
                null,
                null,
                "Mehrere mögliche Treffer",
                ReviewStatus.PENDING,
                Instant.now()));

        var found = documentAnalysisRepository.findByDocumentId(document.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getMatchStatus()).isEqualTo(MatchStatus.AMBIGUOUS);
    }
}
