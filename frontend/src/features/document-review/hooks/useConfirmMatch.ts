import { useMutation, useQueryClient } from "@tanstack/react-query";
import { ApiError } from "../../../shared/api/httpClient";
import { useToast } from "../../../shared/feedback/useToast";
import { de } from "../../../shared/i18n/de";
import { confirmMatch, type ConfirmMatchInput } from "../api/documentReviewApi";

export function useConfirmMatch() {
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  return useMutation({
    mutationFn: (input: ConfirmMatchInput) => confirmMatch(input),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["pending-reviews"] });
      queryClient.invalidateQueries({ queryKey: ["employee-documents"] });
      queryClient.invalidateQueries({ queryKey: ["document-categories"] });
      showToast({ severity: "success", message: de.review.confirmationSuccess });
    },
    onError: (error) => {
      showToast({
        severity: "error",
        message: error instanceof ApiError ? error.message : de.review.confirmationError,
      });
    },
  });
}
