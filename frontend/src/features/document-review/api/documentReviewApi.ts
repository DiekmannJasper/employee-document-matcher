import { fetchJson } from "../../../shared/api/httpClient";
import type { DocumentSummary } from "../../employee-documents/api/employeeDocumentApi";

export type MatchStatus = "MATCHED" | "NO_MATCH" | "AMBIGUOUS";

export type ConfidenceLevel = "HIGH" | "MEDIUM" | "LOW" | "NONE";

export interface PendingReview {
  readonly documentId: string;
  readonly originalFilename: string;
  readonly matchStatus: MatchStatus;
  readonly suggestedEmployeeId: string | null;
  readonly evidence: string;
  readonly systemScore: ConfidenceLevel;
  readonly suggestedCategoryId: string | null;
  readonly suggestedCategoryName: string | null;
  readonly llmConfidence: ConfidenceLevel;
  readonly uploadedAt: string;
}

export interface ConfirmMatchInput {
  readonly documentId: string;
  readonly employeeId: string;
  readonly categoryId?: string | null;
  readonly newCategoryName?: string | null;
}

export function fetchPendingReviews(): Promise<PendingReview[]> {
  return fetchJson<PendingReview[]>("/api/documents/pending-review");
}

export function confirmMatch({ documentId, employeeId, categoryId, newCategoryName }: ConfirmMatchInput): Promise<DocumentSummary> {
  return fetchJson<DocumentSummary>(`/api/documents/${documentId}/confirmation`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ employeeId, categoryId, newCategoryName }),
  });
}
