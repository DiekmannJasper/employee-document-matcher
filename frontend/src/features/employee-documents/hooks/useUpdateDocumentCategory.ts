import { useMutation, useQueryClient } from "@tanstack/react-query";
import { ApiError } from "../../../shared/api/httpClient";
import { useToast } from "../../../shared/feedback/useToast";
import { de } from "../../../shared/i18n/de";
import { updateEmployeeDocumentCategory, type UpdateDocumentCategoryInput } from "../api/employeeDocumentApi";

export function useUpdateDocumentCategory() {
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  return useMutation({
    mutationFn: (input: UpdateDocumentCategoryInput) => updateEmployeeDocumentCategory(input),
    onSuccess: (document, input) => {
      queryClient.invalidateQueries({ queryKey: ["employee-documents", input.employeeId] });
      showToast({
        severity: "success",
        message: input.categoryName
          ? de.documents.movedToCategory(document.originalFilename, input.categoryName)
          : de.documents.movedToUnassigned(document.originalFilename),
      });
    },
    onError: (error) => {
      showToast({
        severity: "error",
        message: error instanceof ApiError ? error.message : de.documents.moveError,
      });
    },
  });
}
