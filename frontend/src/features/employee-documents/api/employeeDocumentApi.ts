import { fetchJson } from "../../../shared/api/httpClient";

export interface DocumentSummary {
  readonly id: string;
  readonly originalFilename: string;
  readonly categoryId: string | null;
  readonly uploadedAt: string;
}

export function fetchEmployeeDocuments(employeeId: string): Promise<DocumentSummary[]> {
  return fetchJson<DocumentSummary[]>(`/api/employees/${employeeId}/documents`);
}
