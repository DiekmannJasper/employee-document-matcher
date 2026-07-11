import { useMutation, useQueryClient } from "@tanstack/react-query";
import { confirmMatch, type ConfirmMatchInput } from "../api/documentReviewApi";

export function useConfirmMatch() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (input: ConfirmMatchInput) => confirmMatch(input),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["pending-reviews"] });
      queryClient.invalidateQueries({ queryKey: ["employee-documents"] });
      queryClient.invalidateQueries({ queryKey: ["document-categories"] });
    },
  });
}
