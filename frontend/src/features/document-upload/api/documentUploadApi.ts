import { fetchJson } from "../../../shared/api/httpClient";

export type DocumentStatus = "UPLOADED" | "ASSIGNED";

export interface DocumentUploadResponse {
  readonly id: string;
  readonly originalFilename: string;
  readonly status: DocumentStatus;
  readonly uploadedAt: string;
  readonly assignedEmployeeName: string | null;
  readonly assignedCategoryName: string | null;
}

export function uploadDocument(file: File, signal: AbortSignal): Promise<DocumentUploadResponse> {
  const formData = new FormData();
  formData.append("file", file);
  return fetchJson<DocumentUploadResponse>("/api/documents", { method: "POST", body: formData, signal });
}
