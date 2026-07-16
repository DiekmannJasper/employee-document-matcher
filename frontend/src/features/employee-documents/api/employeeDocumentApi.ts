import { fetchJson } from "../../../shared/api/httpClient";

export interface DocumentSummary {
  readonly id: string;
  readonly originalFilename: string;
  readonly categoryId: string | null;
  readonly contentType: string;
  readonly uploadedAt: string;
}

export interface UpdateDocumentCategoryInput {
  readonly employeeId: string;
  readonly documentId: string;
  readonly categoryId: string | null;
  readonly categoryName: string | null;
}

export function fetchEmployeeDocuments(employeeId: string): Promise<DocumentSummary[]> {
  return fetchJson<DocumentSummary[]>(`/api/employees/${employeeId}/documents`);
}

export function getEmployeeDocumentFileUrl(employeeId: string, documentId: string): string {
  return `/api/employees/${employeeId}/documents/${documentId}/file`;
}

export function updateEmployeeDocumentCategory({
  employeeId,
  documentId,
  categoryId,
}: UpdateDocumentCategoryInput): Promise<DocumentSummary> {
  return fetchJson<DocumentSummary>(`/api/employees/${employeeId}/documents/${documentId}/category`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ categoryId }),
  });
}
