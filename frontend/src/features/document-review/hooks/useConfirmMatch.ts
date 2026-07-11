import { useMutation, useQueryClient } from "@tanstack/react-query";
import { confirmMatch } from "../api/documentReviewApi";

interface ConfirmVariables {
  readonly documentId: string;
  readonly employeeId: string;
}

export function useConfirmMatch() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ documentId, employeeId }: ConfirmVariables) => confirmMatch(documentId, employeeId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["pending-reviews"] });
      queryClient.invalidateQueries({ queryKey: ["employee-documents"] });
    },
  });
}
