# ADR 0001: Application architecture

- Status: Accepted
- Date: 2026-07-11

## Context

The challenge asks for a small application that accepts a PDF and checks whether
it matches one of ten people. The surrounding HR use case benefits from showing
where a confirmed document is stored, but the solution must remain small enough
to explain and test thoroughly.

## Decision

### System shape

Use a modular Spring Boot monolith behind a React single-page application. This
keeps deployment and transactions simple while retaining clear business-module
boundaries.

### Backend

- Java 21, Spring Boot 3 and Maven
- Spring Web, Validation, Data JPA and Actuator
- PostgreSQL with Flyway migrations
- Apache PDFBox for text extraction
- JUnit 5, AssertJ, Mockito and Testcontainers
- REST with explicit DTOs and Problem Details error responses

PDF content is stored through a `DocumentStorage` port. The MVP adapter writes to
a configured local directory. PostgreSQL stores document metadata, status and
analysis results, not the PDF bytes.

### Frontend

- React, TypeScript and Vite
- Material UI and MUI X for accessible interactive components
- Tailwind CSS for layout and local composition
- React Router, TanStack Query and React Hook Form
- Vitest and React Testing Library

Shared components are introduced for genuinely repeated behavior or stable design
primitives. Feature-specific components remain in their feature. `useMemo`,
`useCallback`, `React.memo` and effects are used only when their semantics or
measured render cost justify them; they are not applied mechanically.

### Matching and classification

Employee matching is deterministic and explainable. The normalized PDF text is
checked against all ten employees and produces `MATCHED`, `NO_MATCH`,
`AMBIGUOUS`, or `UNREADABLE`.

Document classification is behind a `DocumentClassifier` port. Rule-based and
mock adapters keep local development deterministic. An optional LLM adapter may:

- select an existing category;
- suggest a new category;
- return evidence, a reason and an uncalibrated confidence value.

It may not create folders or assign documents. Both actions require explicit user
confirmation. Inputs and outputs are constrained and validated server-side.

## Consequences

- The system is straightforward to run, test and discuss.
- Storage and classifier providers can be replaced without changing domain rules.
- PostgreSQL and local storage add setup work but demonstrate realistic boundaries.
- OCR, authentication, malware scanning and cloud storage remain production work.
- Combining MUI and Tailwind requires a documented styling boundary to avoid
  conflicting declarations.
