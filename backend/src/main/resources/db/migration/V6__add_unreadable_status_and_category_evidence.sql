ALTER TABLE document_analyses
    DROP CONSTRAINT document_analyses_match_status_check;

ALTER TABLE document_analyses
    ADD CONSTRAINT document_analyses_match_status_check
        CHECK (match_status IN ('MATCHED', 'NO_MATCH', 'AMBIGUOUS', 'UNREADABLE'));

ALTER TABLE document_analyses
    ADD COLUMN category_evidence TEXT;
