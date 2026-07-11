package com.jasper.documentmatcher.matching;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class PersonMatcher {

    public PersonMatchResult match(String documentText, List<MatchCandidate> candidates) {
        var normalizedText = normalize(documentText);

        var hits = candidates.stream()
                .filter(candidate -> containsFullName(normalizedText, normalize(candidate.fullName())))
                .toList();

        if (hits.isEmpty()) {
            return PersonMatchResult.noMatch();
        }

        if (hits.size() > 1) {
            var names = hits.stream().map(MatchCandidate::fullName).toList();
            return PersonMatchResult.ambiguous(
                    hits.stream().map(MatchCandidate::employeeId).toList(),
                    "Mehrere Namen im Dokument gefunden: " + String.join(", ", names));
        }

        var hit = hits.getFirst();
        return PersonMatchResult.matched(hit.employeeId(), "Name im Dokument gefunden: '" + hit.fullName() + "'");
    }

    private boolean containsFullName(String normalizedText, String normalizedName) {
        if (normalizedName.isBlank()) {
            return false;
        }

        var pattern = Pattern.compile(
                "(?<!\\p{L})" + Pattern.quote(normalizedName) + "(?!\\p{L})", Pattern.UNICODE_CHARACTER_CLASS);
        return pattern.matcher(normalizedText).find();
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
