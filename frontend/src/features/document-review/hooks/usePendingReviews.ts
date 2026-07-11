import { useQuery } from "@tanstack/react-query";
import { fetchPendingReviews } from "../api/documentReviewApi";

export function usePendingReviews() {
  return useQuery({
    queryKey: ["pending-reviews"],
    queryFn: fetchPendingReviews,
  });
}
