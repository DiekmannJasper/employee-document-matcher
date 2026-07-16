ALTER TABLE documents
    ADD COLUMN content_type VARCHAR(150) NOT NULL DEFAULT 'application/pdf';

ALTER TABLE documents
    ALTER COLUMN content_type DROP DEFAULT;
