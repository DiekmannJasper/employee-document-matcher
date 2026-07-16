import { useMutation, useQueryClient } from "@tanstack/react-query";
import { ApiError } from "../../../shared/api/httpClient";
import { useToast } from "../../../shared/feedback/useToast";
import { de } from "../../../shared/i18n/de";
import { uploadDocument, type DocumentUploadResponse } from "../api/documentUploadApi";

interface UploadVariables {
  readonly file: File;
  readonly signal: AbortSignal;
}

export function useUploadDocument() {
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  return useMutation<DocumentUploadResponse, unknown, UploadVariables>({
    mutationFn: ({ file, signal }) => uploadDocument(file, signal),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["pending-reviews"] });
      queryClient.invalidateQueries({ queryKey: ["employee-documents"] });
      showToast({ severity: "success", message: uploadSuccessMessage(data) });
    },
    onError: (error) => {
      if (error instanceof DOMException && error.name === "AbortError") {
        return;
      }

      showToast({
        severity: "error",
        message: error instanceof ApiError ? error.message : de.upload.error,
      });
    },
  });
}

export function uploadSuccessMessage(data: DocumentUploadResponse) {
  if (data.assignedEmployeeName && data.assignedCategoryName) {
    return de.upload.assignedToEmployeeAndCategory(
      data.originalFilename,
      data.assignedEmployeeName,
      data.assignedCategoryName,
    );
  }

  if (data.assignedEmployeeName) {
    return de.upload.assignedToEmployee(data.originalFilename, data.assignedEmployeeName);
  }

  return de.upload.success(data.originalFilename);
}
