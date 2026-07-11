package com.jasper.documentmatcher.classification;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RuleBasedDocumentClassifier implements DocumentClassifier {

    private static final BigDecimal EXISTING_CATEGORY_CONFIDENCE = new BigDecimal("0.90");
    private static final BigDecimal NEW_CATEGORY_CONFIDENCE = new BigDecimal("0.60");

    private static final List<CategoryRule> RULES = List.of(
            new CategoryRule("CONTRACT", "Verträge", List.of("arbeitsvertrag", "vertrag")),
            new CategoryRule("SALARY", "Gehalt", List.of("gehaltsabrechnung", "lohnabrechnung", "gehalt", "lohn")),
            new CategoryRule("REFERENCE", "Zeugnisse", List.of("arbeitszeugnis", "zeugnis")),
            new CategoryRule("CERTIFICATE", "Bescheinigungen", List.of("bescheinigung")),
            new CategoryRule("TERMINATION", "Kündigungen", List.of("kündigung", "kuendigung")));

    private final ClassificationMode mode;

    public RuleBasedDocumentClassifier(
            @Value("${app.classification.mode:RULE_BASED}") ClassificationMode mode) {
        this.mode = mode;
    }

    @Override
    public ClassificationResult classify(String documentText, List<CategoryCandidate> categories) {
        if (mode == ClassificationMode.SIMULATED_FAILURE) {
            return ClassificationResult.manualReview("Simulierter Klassifikations-Ausfall.");
        }

        var normalizedText = normalize(documentText);
        var validCandidates = categories.stream()
                .filter(candidate -> candidate.categoryId() != null && candidate.code() != null)
                .toList();

        var hits = RULES.stream()
                .filter(rule -> matchesKeyword(normalizedText, rule))
                .toList();

        if (hits.isEmpty()) {
            return ClassificationResult.manualReview("Kein bekanntes Dokumentmuster erkannt.");
        }

        if (hits.size() > 1) {
            var names = hits.stream().map(CategoryRule::displayName).toList();
            return ClassificationResult.manualReview(
                    "Mehrere mögliche Dokumentarten erkannt: " + String.join(", ", names));
        }

        var rule = hits.getFirst();
        var matchedKeyword = matchedKeyword(normalizedText, rule);
        var evidence = "Schlüsselwort erkannt: '" + matchedKeyword + "'";

        return validCandidates.stream()
                .filter(candidate -> candidate.code().equalsIgnoreCase(rule.code()))
                .findFirst()
                .map(candidate -> ClassificationResult.useExisting(candidate, EXISTING_CATEGORY_CONFIDENCE, evidence))
                .orElseGet(() -> ClassificationResult.suggestNew(rule.displayName(), NEW_CATEGORY_CONFIDENCE, evidence));
    }

    private boolean matchesKeyword(String normalizedText, CategoryRule rule) {
        return rule.keywords().stream().anyMatch(normalizedText::contains);
    }

    private String matchedKeyword(String normalizedText, CategoryRule rule) {
        return rule.keywords().stream()
                .filter(normalizedText::contains)
                .findFirst()
                .orElseThrow();
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
