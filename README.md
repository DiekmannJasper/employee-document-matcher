# Employee Document Matcher

Coding challenge for assigning uploaded HR documents to one of ten employees and
organizing confirmed documents in a controlled folder structure.

## MVP

The MVP provides:

- a dashboard containing ten synthetic employees;
- an employee detail view with document categories;
- secure upload and text extraction for text-based PDF files;
- deterministic matching against the known employee list;
- explicit handling of no match and ambiguous matches;
- human confirmation before a document is assigned;
- optional category suggestions through a provider-independent classifier.

The MVP does not include authentication, production cloud storage, OCR, automatic
retention policies, or unattended AI decisions. These are documented production
concerns rather than hidden assumptions.

## Architecture

The application is a modular monolith with a separate web client:

```text
React + TypeScript
Material UI + Tailwind CSS
        |
        | REST
        v
Java 21 + Spring Boot
        |
        +-- PostgreSQL
        +-- DocumentStorage port -> local storage for the MVP
        +-- DocumentClassifier port -> rules/mock/optional LLM
```

Backend modules follow business capabilities (`employee`, `document`, `matching`,
and `classification`). The PDF bytes are stored behind a storage abstraction;
PostgreSQL contains metadata and analysis results only.

See [ADR 0001](docs/adr/0001-application-architecture.md) for the decisions and
trade-offs.

## Repository layout

```text
backend/        Spring Boot application
frontend/       React application
docs/           Architecture and project documentation
```

Setup and run instructions will be added as the applications are initialized.

## Data and AI policy

- The repository and demo use synthetic employees and synthetic documents only.
- Extracted document text, personal data, and API keys must not be logged.
- Deterministic matching is the primary mechanism for identifying an employee.
- A classifier may suggest an existing or new category, but cannot create one or
  assign a document without user confirmation.
- Model confidence is not presented as a calibrated probability. A separate,
  explainable system score combines deterministic signals where appropriate.

## Delivery workflow

Work is tracked in GitHub Issues. Each change should be implemented on a focused
branch and merged through a pull request that references its issue.
