package com.jasper.documentmatcher.matching;

import static org.assertj.core.api.Assertions.assertThat;

import com.jasper.documentmatcher.document.MatchStatus;
import java.text.Normalizer;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PersonMatcherTest {

    private final PersonMatcher matcher = new PersonMatcher();

    private static final UUID ANNA_ID = UUID.randomUUID();
    private static final UUID DAVID_ID = UUID.randomUUID();

    private static final List<MatchCandidate> TEN_EMPLOYEES = List.of(
            new MatchCandidate(ANNA_ID, "Anna Müller"), new MatchCandidate(DAVID_ID, "David Schneider"));

    @Test
    void matchesExactFullName() {
        var result = matcher.match("Arbeitsvertrag für Anna Müller, wohnhaft in Freiburg.", TEN_EMPLOYEES);

        assertThat(result.status()).isEqualTo(MatchStatus.MATCHED);
        assertThat(result.matchedEmployeeId()).isEqualTo(ANNA_ID);
    }

    @Test
    void isCaseInsensitive() {
        var result = matcher.match("ARBEITSVERTRAG FÜR ANNA MÜLLER", TEN_EMPLOYEES);

        assertThat(result.status()).isEqualTo(MatchStatus.MATCHED);
        assertThat(result.matchedEmployeeId()).isEqualTo(ANNA_ID);
    }

    @Test
    void normalizesWhitespaceAcrossLineBreaksAndTabs() {
        var result = matcher.match("Vertrag\n\nAnna\t Müller\nUnterschrift", TEN_EMPLOYEES);

        assertThat(result.status()).isEqualTo(MatchStatus.MATCHED);
        assertThat(result.matchedEmployeeId()).isEqualTo(ANNA_ID);
    }

    @Test
    void normalizesDecomposedUnicodeCharacters() {
        // NFD decomposes precomposed letters (e.g. u-umlaut) into a base letter plus a
        // combining mark, as some PDF text extractors emit. The matcher must still treat
        // this as the same name as the precomposed form stored for the employee.
        var decomposedName = Normalizer.normalize("Anna Müller", Normalizer.Form.NFD);

        var result = matcher.match("Vertrag: " + decomposedName, TEN_EMPLOYEES);

        assertThat(result.status()).isEqualTo(MatchStatus.MATCHED);
        assertThat(result.matchedEmployeeId()).isEqualTo(ANNA_ID);
    }

    @Test
    void returnsNoMatchWhenNoEmployeeNameAppears() {
        var result = matcher.match("Dieses Dokument nennt keinen bekannten Namen.", TEN_EMPLOYEES);

        assertThat(result.status()).isEqualTo(MatchStatus.NO_MATCH);
        assertThat(result.matchedEmployeeId()).isNull();
    }

    @Test
    void returnsNoMatchForFirstNameOnly() {
        var result = matcher.match("Anna hat unterschrieben.", TEN_EMPLOYEES);

        assertThat(result.status()).isEqualTo(MatchStatus.NO_MATCH);
    }

    @Test
    void doesNotMatchSubstringOfALongerName() {
        var result = matcher.match("Vertrag für Annalena Müller-Schmidt", TEN_EMPLOYEES);

        assertThat(result.status()).isEqualTo(MatchStatus.NO_MATCH);
    }

    @Test
    void returnsAmbiguousWhenMultipleEmployeeNamesAppear() {
        var result = matcher.match("Zeugen: Anna Müller und David Schneider", TEN_EMPLOYEES);

        assertThat(result.status()).isEqualTo(MatchStatus.AMBIGUOUS);
        assertThat(result.matchedEmployeeId()).isNull();
        assertThat(result.candidateEmployeeIds()).containsExactlyInAnyOrder(ANNA_ID, DAVID_ID);
    }

    @Test
    void returnsNoMatchForBlankDocumentText() {
        var result = matcher.match("   ", TEN_EMPLOYEES);

        assertThat(result.status()).isEqualTo(MatchStatus.NO_MATCH);
    }

    @Test
    void returnsNoMatchWhenCandidateListIsEmpty() {
        var result = matcher.match("Anna Müller", List.of());

        assertThat(result.status()).isEqualTo(MatchStatus.NO_MATCH);
    }
}
