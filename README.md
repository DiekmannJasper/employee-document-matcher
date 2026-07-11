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
docs/demo-pdfs/ Synthetic PDFs for demoing match, no-match and ambiguous cases
```

## Quick start

Requires JDK 21, Maven 3.9+, Node.js 22+, and Docker Desktop.

```bash
# 1. Database
docker compose up -d postgres

# 2. Backend (new terminal, from backend/)
mvn spring-boot:run

# 3. Frontend (new terminal, from frontend/)
npm install
npm run dev
```

Open http://localhost:5173. The ten synthetic employees are seeded by Flyway on
first backend start. See [backend/README.md](backend/README.md) and
[frontend/README.md](frontend/README.md) for test commands and configuration
details (`.env.example` lists the overridable environment variables).

## Demo

[`docs/demo-pdfs/`](docs/demo-pdfs/) contains three synthetic PDFs covering the
matcher's three outcomes — upload each through the "PDF hochladen" button and
then confirm or correct the result on the "Prüffälle" page:

| File | Expected outcome |
| --- | --- |
| `match-anna-mueller.pdf` | `MATCHED` — contains exactly one employee's full name (Anna Müller) |
| `no-match-generic-letter.pdf` | `NO_MATCH` — contains no employee name |
| `ambiguous-two-names.pdf` | `AMBIGUOUS` — contains two employee names (David Schneider, Laura Hoffmann) |

## Assumptions and limitations

- The employee list is fixed to the ten synthetic, seeded records; there is no
  employee management UI.
- Matching only works on text-based PDFs. Scanned/image-only PDFs are read as
  empty text and reported as `NO_MATCH`, since OCR is out of scope for the MVP.
- Exactly one PDF per upload; no bulk/multi-file upload.
- The document category set is fixed and seeded. The `DocumentClassifier` port
  and its rule-based mock (#20) exist and are unit-tested, but are not yet
  called during upload or exposed through an endpoint — surfacing and
  confirming category suggestions in the UI is backlog issue #23.
- No authentication/authorization — this is a local, single-user demo.
- The rule-based classifier (#20) is a deliberately simple keyword matcher, not
  a real LLM call, since no API key is available in this environment; it is
  isolated behind a provider-independent port so a real adapter (#22, backlog)
  can be added later without touching callers.

## Data and AI policy

- The repository and demo use synthetic employees and synthetic documents only.
- Extracted document text, personal data, and API keys must not be logged.
- Deterministic matching is the primary mechanism for identifying an employee.
- A classifier may suggest an existing or new category, but cannot create one or
  assign a document without user confirmation.
- Model confidence is not presented as a calibrated probability. A separate,
  explainable system score combines deterministic signals where appropriate.

## AI-assisted development

This project was built with AI assistance, used transparently and reviewed at
every step:

- **Planning:** an initial ticket/issue breakdown was drafted with OpenAI Codex
  from the challenge brief, then reviewed and re-scoped by hand (trimmed to a
  deterministic must-path first, LLM-classification work reduced to a
  provider-independent port with a rule-based mock since no API key was
  available).
- **Implementation:** Claude Code implemented the GitHub issues iteratively —
  one focused change at a time, with tests run and, where practical, the change
  verified live against the running dev servers before moving to the next
  issue.
- Every architectural and implementation decision in this repository can be
  explained and justified in conversation; nothing here is unattended
  generated code the author cannot account for.

## Delivery workflow

Work is tracked in GitHub Issues. Each change should be implemented on a focused
branch and merged through a pull request that references its issue.
