import { useMutation } from "@tanstack/react-query";
import { uploadDocument, type DocumentUploadResponse } from "../api/documentUploadApi";

interface UploadVariables {
  readonly file: File;
  readonly signal: AbortSignal;
}

export function useUploadDocument() {
  return useMutation<DocumentUploadResponse, unknown, UploadVariables>({
    mutationFn: ({ file, signal }) => uploadDocument(file, signal),
  });
}
