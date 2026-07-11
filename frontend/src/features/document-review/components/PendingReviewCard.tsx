import { Alert, Button, Card, CardActions, CardContent, Stack, Typography } from "@mui/material";
import { useState } from "react";
import { ApiError } from "../../../shared/api/httpClient";
import type { DocumentCategory } from "../../document-categories/api/documentCategoryApi";
import type { Employee } from "../../employees/api/employeeApi";
import type { PendingReview } from "../api/documentReviewApi";
import { useConfirmMatch } from "../hooks/useConfirmMatch";
import { CategoryPicker, type CategorySelection } from "./CategoryPicker";
import { ConfidenceChip } from "./ConfidenceChip";
import { EmployeePicker } from "./EmployeePicker";
import { MatchStatusChip } from "./MatchStatusChip";

interface PendingReviewCardProps {
  readonly review: PendingReview;
  readonly employees: readonly Employee[];
  readonly categories: readonly DocumentCategory[];
}

export function PendingReviewCard({ review, employees, categories }: PendingReviewCardProps) {
  const [selectedEmployeeId, setSelectedEmployeeId] = useState<string | null>(review.suggestedEmployeeId);
  const [categorySelection, setCategorySelection] = useState<CategorySelection>({
    categoryId: review.suggestedCategoryId,
    newCategoryName: review.suggestedCategoryId ? "" : (review.suggestedCategoryName ?? ""),
  });
  const confirmMutation = useConfirmMatch();

  function handleConfirm() {
    if (!selectedEmployeeId) {
      return;
    }
    confirmMutation.mutate({
      documentId: review.documentId,
      employeeId: selectedEmployeeId,
      categoryId: categorySelection.categoryId,
      newCategoryName: categorySelection.newCategoryName.trim() || null,
    });
  }

  const isDisabled = confirmMutation.isPending || confirmMutation.isSuccess;

  return (
    <Card variant="outlined">
      <CardContent>
        <Stack spacing={1.5}>
          <Stack direction="row" spacing={1} sx={{ alignItems: "center", justifyContent: "space-between" }}>
            <Typography sx={{ fontWeight: 600 }}>{review.originalFilename}</Typography>
            <MatchStatusChip matchStatus={review.matchStatus} />
          </Stack>
          <Typography variant="body2" color="text.secondary">
            {review.evidence}
          </Typography>
          <Stack direction="row" spacing={1}>
            <ConfidenceChip label="System-Score" level={review.systemScore} />
            <ConfidenceChip label="KI-Konfidenz" level={review.llmConfidence} />
          </Stack>
          <EmployeePicker
            employees={employees}
            selectedEmployeeId={selectedEmployeeId}
            onChange={setSelectedEmployeeId}
            disabled={isDisabled}
          />
          {review.suggestedCategoryName && !review.suggestedCategoryId && (
            <Typography variant="body2" color="text.secondary">
              Vorschlag für neue Kategorie: "{review.suggestedCategoryName}"
            </Typography>
          )}
          <CategoryPicker
            categories={categories}
            selection={categorySelection}
            onChange={setCategorySelection}
            disabled={isDisabled}
          />
          {confirmMutation.isError && (
            <Alert severity="error">
              {confirmMutation.error instanceof ApiError
                ? confirmMutation.error.message
                : "Bestätigung fehlgeschlagen."}
            </Alert>
          )}
          {confirmMutation.isSuccess && <Alert severity="success">Zuordnung bestätigt.</Alert>}
        </Stack>
      </CardContent>
      <CardActions>
        <Button variant="contained" onClick={handleConfirm} disabled={!selectedEmployeeId || isDisabled}>
          Bestätigen
        </Button>
      </CardActions>
    </Card>
  );
}
