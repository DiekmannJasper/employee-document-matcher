import { Stack, Typography } from "@mui/material";
import { useDocumentCategories } from "../../features/document-categories/hooks/useDocumentCategories";
import { PendingReviewCard } from "../../features/document-review/components/PendingReviewCard";
import { usePendingReviews } from "../../features/document-review/hooks/usePendingReviews";
import { useEmployees } from "../../features/employees/hooks/useEmployees";
import { EmptyState } from "../../shared/components/EmptyState/EmptyState";
import { ErrorState } from "../../shared/components/ErrorState/ErrorState";
import { LoadingState } from "../../shared/components/LoadingState/LoadingState";
import { PageContainer } from "../../shared/components/PageContainer/PageContainer";
import { de } from "../../shared/i18n/de";

export function ReviewPage() {
  const pendingReviewsQuery = usePendingReviews();
  const employeesQuery = useEmployees();
  const categoriesQuery = useDocumentCategories();

  const isPending = pendingReviewsQuery.isPending || employeesQuery.isPending || categoriesQuery.isPending;
  const isError = pendingReviewsQuery.isError || employeesQuery.isError || categoriesQuery.isError;

  return (
    <PageContainer>
      <Typography variant="h5" component="h2" gutterBottom>
        {de.review.title}
      </Typography>
      {isPending && <LoadingState message={de.review.loading} />}
      {isError && (
        <ErrorState
          message={de.review.loadError}
          onRetry={() => {
            pendingReviewsQuery.refetch();
            employeesQuery.refetch();
            categoriesQuery.refetch();
          }}
        />
      )}
      {!isPending && !isError && pendingReviewsQuery.data?.length === 0 && (
        <EmptyState message={de.review.empty} />
      )}
      {!isPending && !isError && pendingReviewsQuery.data && pendingReviewsQuery.data.length > 0 && (
        <Stack spacing={2}>
          {pendingReviewsQuery.data.map((review) => (
            <PendingReviewCard
              key={review.documentId}
              review={review}
              employees={employeesQuery.data ?? []}
              categories={categoriesQuery.data ?? []}
            />
          ))}
        </Stack>
      )}
    </PageContainer>
  );
}
