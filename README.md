# Employee Document Matcher

Coding challenge for assigning uploaded HR documents to one of ten employees and
organizing confirmed documents in a controlled folder structure.

## MVP

The MVP provides:

- a dashboard containing ten synthetic employees;
- an employee detail view with document categories;
- secure upload and text extraction for text-based PDF, Word (`.docx`), and XML files;
- mocked external document import for systems such as DATEV;
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
matcher's three outcomes — upload each through the "Manuell hochladen" button and
then confirm or correct the result on the "Prüfung" page:

| File | Expected outcome |
| --- | --- |
| `match-anna-mueller.pdf` | `MATCHED` — contains exactly one employee's full name (Anna Müller) |
| `no-match-generic-letter.pdf` | `NO_MATCH` — contains no employee name |
| `ambiguous-two-names.pdf` | `AMBIGUOUS` — contains two employee names (David Schneider, Laura Hoffmann) |
| `auto-contract-david-schneider.pdf` | auto-assigned to David Schneider, category `Verträge` |
| `auto-salary-laura-hoffmann.pdf` | auto-assigned to Laura Hoffmann, category `Gehalt` |
| `auto-reference-jonas-fischer.pdf` | auto-assigned to Jonas Fischer, category `Zeugnisse` |
| `auto-certificate-miriam-weber.pdf` | auto-assigned to Miriam Weber, category `Bescheinigungen` |
| `auto-termination-felix-wagner.pdf` | auto-assigned to Felix Wagner; category remains unassigned unless `Kündigungen` exists |
| `review-no-employee-salary.pdf` | review case — salary category signal, but no employee match |
| `review-ambiguous-contract-two-employees.pdf` | review case — contract category signal, but multiple employee names |

## How matching and folder assignment work

The matching flow is intentionally conservative and explainable. The system does
not infer the employee from a filename, folder name, or free-form AI guess. It
extracts text from the document, compares that text against the known employee
list, and automatically assigns the document only when exactly one known full
name is found. No-match, ambiguous, and unreadable documents stay in manual
review.

### Supported file formats

Manual upload and external import accept three formats: PDF, Word (`.docx`),
and XML. The accepted format is detected from magic bytes
(`DocumentFormat.detect`), not from the client-supplied content-type - browsers
and OS file pickers are inconsistent about what content-type they report for
less common formats, and a spoofed content-type header wouldn't have added any
real security anyway.

Each format has its own `DocumentTextExtractor` implementation, resolved at
analysis time by `DocumentTextExtractorResolver`:

| Format | Extractor | How text is extracted |
| --- | --- | --- |
| PDF | `PdfTextExtractor` | Apache PDFBox (`PDFTextStripper`) |
| Word (`.docx`) | `DocxTextExtractor` | reads the `word/document.xml` ZIP entry directly and concatenates `<w:t>` runs |
| XML | `XmlTextExtractor` | concatenates all XML text nodes |

The `.docx` and XML extractors are hand-rolled on top of the JDK's built-in
StAX parser (`javax.xml.stream`) rather than pulling in Apache POI, which would
add a large dependency chain (`xmlbeans`, `commons-compress`, ...) for text
extraction this narrow. Both use a hardened `XMLInputFactory`
(`SafeXmlInputFactory`) with DTD processing and external entity resolution
disabled, so parsing an uploaded file can't be turned into an XXE attack.

**Scanned documents and images (e.g. JPG) are out of scope.** All three
supported formats carry machine-readable text; there is nothing to "extract"
from a photo or a scanned page without OCR. Adding OCR (e.g. Tesseract) would
be a materially different feature - a new inference step with its own
accuracy/cost trade-offs - rather than another `DocumentTextExtractor`, so it's
tracked as a deliberate MVP boundary rather than bolted on.

### Upload and analysis flow

1. The uploaded file is validated and stored through the `DocumentStorage`
   abstraction.
2. Local MVP storage writes the bytes under a random technical key that
   preserves the detected format's extension, e.g. `<uuid>.pdf` or
   `<uuid>.docx`; the original filename and detected content-type are stored
   only as metadata.
3. `DocumentTextExtractorResolver` picks the matching extractor for the
   detected format and extracts text.
4. If text extraction fails because the file is empty, encrypted, or corrupt,
   the analysis is marked as `UNREADABLE`.
5. If text extraction succeeds, `DocumentAnalysisService` runs both person
   matching and category classification.
6. If exactly one employee is matched, the document is assigned automatically;
   otherwise it stays in review until the user confirms the employee and,
   optionally, the category.

### Employee name matching

`PersonMatcher` receives the extracted document text and a candidate list built
from all seeded employees. Each candidate consists of the employee ID and the
full name, for example `Anna Müller`.

Before comparing, both the PDF text and the candidate name are normalized:

- Unicode normalization with `NFKC`;
- lowercase comparison;
- trimming leading and trailing whitespace;
- collapsing repeated whitespace to a single space.

The matcher then searches for the complete normalized full name. It deliberately
does not match on first name only or last name only, because partial names would
be too risky in an HR context. The regular expression also checks Unicode letter
boundaries, so `Anna Müller` is treated as an independent name and not as part of
a longer word or unrelated name.

Person matching has four possible outcomes:

| Status | Meaning | Automatic assignment? |
| --- | --- | --- |
| `MATCHED` | Exactly one known full employee name was found. | The document is assigned to that employee automatically. |
| `NO_MATCH` | No known full employee name was found. | No employee is suggested. |
| `AMBIGUOUS` | More than one known full employee name was found. | No employee is selected automatically. |
| `UNREADABLE` | Text extraction failed. | Matching is skipped. |

### Category and folder matching

The visible "folders" in the employee detail view are document categories, not
physical filesystem directories. A confirmed document is shown in a folder
because its database row has a `category_id`.

`RuleBasedDocumentClassifier` checks the normalized document text for a small
set of keywords:

| Category | Keywords |
| --- | --- |
| `Verträge` | `arbeitsvertrag`, `vertrag` |
| `Gehalt` | `gehaltsabrechnung`, `lohnabrechnung`, `gehalt`, `lohn` |
| `Zeugnisse` | `arbeitszeugnis`, `zeugnis` |
| `Bescheinigungen` | `bescheinigung` |
| `Kündigungen` | `kündigung`, `kuendigung` |

If exactly one rule matches, the classifier suggests an existing category with
the same code. If no rule matches, the category remains a manual review choice.
If multiple rules match, the result is treated as ambiguous and no category is
chosen automatically.

When a new category name is confirmed, `DocumentCategoryService` first normalizes
the display name and reuses an existing category if the normalized names match.
This prevents near-duplicate folders such as `Kündigungen` and ` kündigungen `.
Only if no matching category exists does the service create a new category and a
stable technical code such as `KUENDIGUNGEN`.

### Human confirmation

For documents with `NO_MATCH`, `AMBIGUOUS`, or `UNREADABLE`, `DocumentReviewService`
is the point where a suggestion becomes an actual assignment. Confirmation
requires an `employeeId`; category assignment is optional. A request may either
reference an existing `categoryId` or provide a new category name, but not both.
Once confirmed, the document status changes to `ASSIGNED`.

For documents with `MATCHED`, `DocumentUploadService` performs the assignment
immediately after analysis. If the classifier also suggested an existing
category, that category is assigned at the same time. The upload toast then shows
which employee and, if available, which category received the document.

### Mocked external document import

The MVP also includes a mocked external document interface under
`/api/external-documents`. It represents systems such as DATEV without requiring
real credentials or network access. The mock exposes a small list of available
documents and an import endpoint:

- `GET /api/external-documents` lists external documents from `DATEV Mock`.
- `POST /api/external-documents/imports` imports one selected document.

Imported documents are generated as text-based PDFs and then passed through the
same ingestion, PDF extraction, matching, category classification, auto-assign,
and review flow as manually uploaded PDFs. This keeps the connector boundary
visible while avoiding a parallel code path.

## Assumptions and limitations

- The employee list is fixed to the ten synthetic, seeded records; there is no
  employee management UI.
- Matching only works on text-based PDF, Word (`.docx`), and XML files (see
  "Supported file formats" above). Scanned/image-only files, encrypted
  documents, and other formats are out of scope; PDFs that are image-only or
  encrypted are reported as `UNREADABLE`, since OCR is out of scope for the
  MVP.
- Exactly one file per upload; no bulk/multi-file upload.
- No authentication/authorization — this is a local, single-user demo.
- The rule-based classifier (#20) is a deliberately simple keyword matcher, not
  a real LLM call, since no API key is available in this environment; it is
  isolated behind a provider-independent port so a real adapter (#22, backlog)
  can be added later without touching callers. The `llmConfidence` /
  `LLM_SUGGESTED` naming is forward-looking for that adapter.

### Known limitations (reviewed, deliberately not addressed in the MVP)

These came out of a code review and are conscious trade-offs rather than
oversights — each would matter in production:

- **Concurrent confirmation race:** two simultaneous confirmations of the same
  document can both pass the `PENDING` check (no optimistic locking/`@Version`
  yet); the sequential case is guarded and returns 409.
- **N+1 query in the pending-review list:** one `findById` per pending
  analysis. Irrelevant at demo scale, would become a join in production.
- **Package dependency cycle** between `document` and `matching` via
  `MatchStatus` (lives in `document`, used by `matching`).
- **No end-to-end integration test** driving upload → analysis → review →
  confirm through the real database; coverage is unit + web-slice tests.
- **`DocumentAnalysis` telescoping constructor** (12 params); a builder was
  considered over-engineering at this size.
- **Category creation race:** two concurrent confirmations with the same new
  category name can both miss the duplicate check; the DB unique constraint on
  `code` catches most collisions.
- **New-category creation is unreachable from the UI.** The backend still
  accepts `newCategoryName` on confirm (a classifier can suggest a category
  that doesn't exist yet, e.g. "Kündigungen"), but the review card was
  simplified to only pick from existing categories. A not-yet-seeded
  suggested category currently has to be mapped to an existing one (e.g.
  "Sonstiges") by the reviewer.
- **DataGrid row navigation is mouse-only**; keyboard users cannot open an
  employee row.
- **Tailwind CSS is configured but barely used** (MUI `sx` covers styling);
  either commit to it or remove it.
- **`ReviewStatus.REJECTED` is modeled but unreachable** — a reject flow is
  backlog issue #25.

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
