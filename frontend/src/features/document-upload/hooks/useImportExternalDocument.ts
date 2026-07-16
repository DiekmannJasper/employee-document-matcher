import { useMutation, useQueryClient } from "@tanstack/react-query";
import { ApiError } from "../../../shared/api/httpClient";
import { useToast } from "../../../shared/feedback/useToast";
import { de } from "../../../shared/i18n/de";
import { importExternalDocument } from "../api/externalDocumentApi";
import { uploadSuccessMessage } from "./useUploadDocument";

export function useImportExternalDocument() {
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  return useMutation({
    mutationFn: (externalDocumentId: string) => importExternalDocument(externalDocumentId),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["pending-reviews"] });
      queryClient.invalidateQueries({ queryKey: ["employee-documents"] });
      if (data.documents.length === 1) {
        showToast({ severity: "success", message: uploadSuccessMessage(data.documents[0]) });
        return;
      }

      showToast({ severity: "success", message: de.externalDocuments.bundleSuccess(data.documents.length) });
    },
    onError: (error) => {
      showToast({
        severity: "error",
        message: error instanceof ApiError ? error.message : de.externalDocuments.importError,
      });
    },
  });
}
