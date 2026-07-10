# ADR 0001: Application architecture

- Status: Accepted
- Date: 2026-07-11

## Context

The challenge asks for a small application that accepts a PDF and checks whether it matches one of ten people. The HR context benefits from showing where a confirmed document is stored while keeping the solution small enough to explain and test thoroughly.

## Decision

Use a modular Spring Boot monolith behind a React single-page application.

### Backend

- Java 21, Spring Boot 3 and Maven
- Spring Web, Validation, Data JPA and Actuator
- PostgreSQL with Flyway migrations
- Apache PDFBox
- JUnit 5, AssertJ, Mockito and Testcontainers
- REST DTOs and Problem Details errors

PDF content is stored through a `DocumentStorage` port. PostgreSQL stores metadata, status and analysis results, not PDF bytes.

### Frontend

- React, TypeScript and Vite
- Material UI and MUI X for accessible components
- Tailwind CSS for layout and composition
- React Router, TanStack Query and React Hook Form
- Vitest and React Testing Library

Shared components cover repeated behavior and stable design primitives. Feature-specific components remain within their feature. Memoization and effects are used only when their semantics or measured cost justify them.

### Matching and classification

Employee matching is deterministic and produces `MATCHED`, `NO_MATCH`, `AMBIGUOUS`, or `UNREADABLE`.

Classification uses a `DocumentClassifier` port with rule-based, mock, and optional LLM adapters. A classifier may select or suggest a category and return evidence, reasoning, and uncalibrated confidence. It cannot create folders or assign documents; users confirm both actions.

## Consequences

- The system is straightforward to run, test and discuss.
- Storage and classifier providers remain replaceable.
- OCR, authentication, malware scanning and cloud storage remain production work.
- MUI and Tailwind require a clear styling boundary.
