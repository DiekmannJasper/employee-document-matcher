import { Stack, Typography } from "@mui/material";
import { PendingReviewCard } from "../../features/document-review/components/PendingReviewCard";
import { usePendingReviews } from "../../features/document-review/hooks/usePendingReviews";
import { useEmployees } from "../../features/employees/hooks/useEmployees";
import { EmptyState } from "../../shared/components/EmptyState/EmptyState";
import { ErrorState } from "../../shared/components/ErrorState/ErrorState";
import { LoadingState } from "../../shared/components/LoadingState/LoadingState";
import { PageContainer } from "../../shared/components/PageContainer/PageContainer";

export function ReviewPage() {
  const pendingReviewsQuery = usePendingReviews();
  const employeesQuery = useEmployees();

  const isPending = pendingReviewsQuery.isPending || employeesQuery.isPending;
  const isError = pendingReviewsQuery.isError || employeesQuery.isError;

  return (
    <PageContainer>
      <Typography variant="h5" component="h2" gutterBottom>
        Offene Prüffälle
      </Typography>
      {isPending && <LoadingState message="Prüffälle werden geladen…" />}
      {isError && (
        <ErrorState
          message="Prüffälle konnten nicht geladen werden."
          onRetry={() => {
            pendingReviewsQuery.refetch();
            employeesQuery.refetch();
          }}
        />
      )}
      {!isPending && !isError && pendingReviewsQuery.data?.length === 0 && (
        <EmptyState message="Es liegen aktuell keine offenen Prüffälle vor." />
      )}
      {!isPending && !isError && pendingReviewsQuery.data && pendingReviewsQuery.data.length > 0 && (
        <Stack spacing={2}>
          {pendingReviewsQuery.data.map((review) => (
            <PendingReviewCard key={review.documentId} review={review} employees={employeesQuery.data ?? []} />
          ))}
        </Stack>
      )}
    </PageContainer>
  );
}
