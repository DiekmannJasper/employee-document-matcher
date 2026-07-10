# Employee Document Matcher

Coding challenge for assigning uploaded HR documents to one of ten employees and organizing confirmed documents in a controlled folder structure.

## MVP

The MVP provides a dashboard with ten synthetic employees, employee detail views with controlled document categories, secure PDF upload, deterministic employee matching, explicit review of ambiguous results, and optional category suggestions behind a provider-independent classifier.

Authentication, OCR, production cloud storage, retention automation, and unattended AI decisions are outside the MVP.

## Architecture

```text
React + TypeScript + Material UI + Tailwind CSS
                       |
                       | REST
                       v
Java 21 + Spring Boot + PostgreSQL
                       |
                       +-- DocumentStorage port
                       +-- DocumentClassifier port
```

The backend is a modular monolith organized by business capability. PDF bytes are stored behind a storage abstraction; PostgreSQL contains metadata and analysis results only.

See [ADR 0001](docs/adr/0001-application-architecture.md) for decisions and trade-offs.

## Repository layout

- `backend/`: Spring Boot application
- `frontend/`: React application
- `docs/`: architecture and project documentation

## Data and AI policy

- Only synthetic employees and documents are used.
- Document text, personal data, and API keys must not be logged.
- Deterministic matching identifies employees.
- A classifier may suggest categories but cannot create a category or assign a document without confirmation.
- Model confidence is not presented as a calibrated probability.

## Delivery workflow

Work is tracked in GitHub Issues. Changes use focused branches and pull requests referencing the corresponding issue.
