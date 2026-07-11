package com.jasper.documentmatcher.matching;

import com.jasper.documentmatcher.document.MatchStatus;
import java.util.List;
import java.util.UUID;

public record PersonMatchResult(
        MatchStatus status, UUID matchedEmployeeId, List<UUID> candidateEmployeeIds, String evidence) {

    static PersonMatchResult matched(UUID employeeId, String evidence) {
        return new PersonMatchResult(MatchStatus.MATCHED, employeeId, List.of(employeeId), evidence);
    }

    static PersonMatchResult noMatch() {
        return new PersonMatchResult(MatchStatus.NO_MATCH, null, List.of(), "Kein Mitarbeitername im Dokument gefunden.");
    }

    static PersonMatchResult ambiguous(List<UUID> candidateEmployeeIds, String evidence) {
        return new PersonMatchResult(MatchStatus.AMBIGUOUS, null, candidateEmployeeIds, evidence);
    }
}
