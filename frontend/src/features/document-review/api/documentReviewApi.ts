import { fetchJson } from "../../../shared/api/httpClient";
import type { DocumentSummary } from "../../employee-documents/api/employeeDocumentApi";

export type MatchStatus = "MATCHED" | "NO_MATCH" | "AMBIGUOUS";

export interface PendingReview {
  readonly documentId: string;
  readonly originalFilename: string;
  readonly matchStatus: MatchStatus;
  readonly suggestedEmployeeId: string | null;
  readonly evidence: string;
  readonly uploadedAt: string;
}

export function fetchPendingReviews(): Promise<PendingReview[]> {
  return fetchJson<PendingReview[]>("/api/documents/pending-review");
}

export function confirmMatch(documentId: string, employeeId: string): Promise<DocumentSummary> {
  return fetchJson<DocumentSummary>(`/api/documents/${documentId}/confirmation`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ employeeId }),
  });
}
