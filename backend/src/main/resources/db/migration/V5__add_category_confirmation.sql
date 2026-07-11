ALTER TABLE documents
    ADD COLUMN category_id UUID REFERENCES document_categories (id);

ALTER TABLE document_analyses
    ADD COLUMN suggested_category_name VARCHAR(150);
