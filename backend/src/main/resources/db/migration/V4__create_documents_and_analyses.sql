CREATE TABLE documents (
    id UUID PRIMARY KEY,
    employee_id UUID REFERENCES employees (id),
    original_filename VARCHAR(255) NOT NULL,
    storage_key VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL CHECK (status IN ('UPLOADED', 'ASSIGNED')),
    uploaded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_documents_employee_id ON documents (employee_id);

CREATE TABLE document_analyses (
    id UUID PRIMARY KEY,
    document_id UUID NOT NULL UNIQUE REFERENCES documents (id),
    match_status VARCHAR(20) NOT NULL CHECK (match_status IN ('MATCHED', 'NO_MATCH', 'AMBIGUOUS')),
    matched_employee_id UUID REFERENCES employees (id),
    match_score NUMERIC(5, 4),
    suggested_category_id UUID REFERENCES document_categories (id),
    category_confidence NUMERIC(5, 4),
    evidence TEXT,
    review_status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (review_status IN ('PENDING', 'CONFIRMED', 'REJECTED')),
    analyzed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
