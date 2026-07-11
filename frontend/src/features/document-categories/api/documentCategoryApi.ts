import { fetchJson } from "../../../shared/api/httpClient";

export type CategoryOrigin = "STANDARD" | "LLM_SUGGESTED" | "MANUAL";

export interface DocumentCategory {
  readonly id: string;
  readonly code: string;
  readonly displayName: string;
  readonly origin: CategoryOrigin;
}

export function fetchDocumentCategories(): Promise<DocumentCategory[]> {
  return fetchJson<DocumentCategory[]>("/api/document-categories");
}
