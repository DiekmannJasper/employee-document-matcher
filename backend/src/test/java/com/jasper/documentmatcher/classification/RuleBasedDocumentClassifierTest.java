package com.jasper.documentmatcher.classification;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RuleBasedDocumentClassifierTest {

    private static final List<CategoryCandidate> SEEDED_CATEGORIES = List.of(
            new CategoryCandidate(UUID.randomUUID(), "CONTRACT", "Verträge"),
            new CategoryCandidate(UUID.randomUUID(), "SALARY", "Gehalt"),
            new CategoryCandidate(UUID.randomUUID(), "REFERENCE", "Zeugnisse"),
            new CategoryCandidate(UUID.randomUUID(), "CERTIFICATE", "Bescheinigungen"),
            new CategoryCandidate(UUID.randomUUID(), "OTHER", "Sonstiges"));

    private final RuleBasedDocumentClassifier classifier = new RuleBasedDocumentClassifier(ClassificationMode.RULE_BASED);

    @Test
    void usesAnExistingCategoryWhenAKeywordMatchesAKnownCandidate() {
        var result = classifier.classify("Dies ist ein Arbeitsvertrag zwischen den Parteien.", SEEDED_CATEGORIES);

        assertThat(result.action()).isEqualTo(ClassificationAction.USE_EXISTING);
        assertThat(result.categoryId())
                .isEqualTo(SEEDED_CATEGORIES.stream()
                        .filter(c -> c.code().equals("CONTRACT"))
                        .findFirst()
                        .orElseThrow()
                        .categoryId());
        assertThat(result.confidence()).isNotNull();
        assertThat(result.evidence()).isNotBlank();
        assertThat(result.reasoning()).isNotBlank();
    }

    @Test
    void suggestsANewCategoryWhenTheMatchedRuleHasNoExistingCandidate() {
        var result = classifier.classify("Hiermit erhalten Sie die Kündigung Ihres Arbeitsverhältnisses.", SEEDED_CATEGORIES);

        assertThat(result.action()).isEqualTo(ClassificationAction.SUGGEST_NEW);
        assertThat(result.categoryId()).isNull();
        assertThat(result.suggestedCategoryName()).isEqualTo("Kündigungen");
        assertThat(result.confidence()).isNotNull();
        assertThat(result.evidence()).isNotBlank();
        assertThat(result.reasoning()).isNotBlank();
    }

    @Test
    void fallsBackToManualReviewWhenNoKnownPatternIsFound() {
        var result = classifier.classify("Ein völlig unstrukturierter Text ohne erkennbares Muster.", SEEDED_CATEGORIES);

        assertThat(result.action()).isEqualTo(ClassificationAction.MANUAL_REVIEW);
        assertThat(result.categoryId()).isNull();
        assertThat(result.confidence()).isNull();
        assertThat(result.reasoning()).isNotBlank();
    }

    @Test
    void doesNotMatchKeywordsEmbeddedInLongerWords() {
        var result = classifier.classify(
                "Die Kollegen vertragen sich gut und die Kündigungsfrist wurde besprochen.", SEEDED_CATEGORIES);

        assertThat(result.action()).isEqualTo(ClassificationAction.MANUAL_REVIEW);
    }

    @Test
    void stillMatchesGermanCompoundsEndingInTheKeyword() {
        var result = classifier.classify("Beiliegend der unterschriebene Mietvertrag.", SEEDED_CATEGORIES);

        assertThat(result.action()).isEqualTo(ClassificationAction.USE_EXISTING);
    }

    @Test
    void fallsBackToManualReviewWhenMultipleDocumentTypesAreDetected() {
        var result = classifier.classify(
                "Dieser Arbeitsvertrag enthält auch eine Gehaltsabrechnung im Anhang.", SEEDED_CATEGORIES);

        assertThat(result.action()).isEqualTo(ClassificationAction.MANUAL_REVIEW);
        assertThat(result.categoryId()).isNull();
    }

    @Test
    void neverReturnsACategoryIdThatWasNotPartOfTheGivenCandidates() {
        var knownIds =
                SEEDED_CATEGORIES.stream().map(CategoryCandidate::categoryId).toList();

        for (var text : List.of(
                "Arbeitsvertrag", "Gehaltsabrechnung", "Arbeitszeugnis", "Bescheinigung", "Kündigung", "Zufallstext")) {
            var result = classifier.classify(text, SEEDED_CATEGORIES);
            if (result.categoryId() != null) {
                assertThat(knownIds).contains(result.categoryId());
            }
        }
    }

    @Test
    void ignoresCandidatesWithMissingIdsOrCodes() {
        var candidatesWithInvalidEntry = List.of(
                new CategoryCandidate(null, "CONTRACT", "Verträge"),
                new CategoryCandidate(UUID.randomUUID(), null, "Ohne Code"));

        var result = classifier.classify("Dies ist ein Arbeitsvertrag.", candidatesWithInvalidEntry);

        assertThat(result.action()).isEqualTo(ClassificationAction.SUGGEST_NEW);
        assertThat(result.categoryId()).isNull();
    }

    @Test
    void simulatedFailureModeAlwaysFallsBackToManualReview() {
        var failingClassifier = new RuleBasedDocumentClassifier(ClassificationMode.SIMULATED_FAILURE);

        var result = failingClassifier.classify("Dies ist ein Arbeitsvertrag.", SEEDED_CATEGORIES);

        assertThat(result.action()).isEqualTo(ClassificationAction.MANUAL_REVIEW);
        assertThat(result.categoryId()).isNull();
        assertThat(result.reasoning()).isNotBlank();
    }
}
