import { fetchJson } from "../../../shared/api/httpClient";
import type { DocumentUploadResponse } from "./documentUploadApi";

export interface ExternalDocument {
  readonly id: string;
  readonly sourceSystem: string;
  readonly filename: string;
  readonly description: string;
  readonly expectedOutcome: string;
}

export interface ExternalDocumentImportResponse {
  readonly documents: readonly DocumentUploadResponse[];
}

export function fetchExternalDocuments(): Promise<ExternalDocument[]> {
  return fetchJson<ExternalDocument[]>("/api/external-documents");
}

export function importExternalDocument(externalDocumentId: string): Promise<ExternalDocumentImportResponse> {
  return fetchJson<ExternalDocumentImportResponse>("/api/external-documents/imports", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ externalDocumentId }),
  });
}
