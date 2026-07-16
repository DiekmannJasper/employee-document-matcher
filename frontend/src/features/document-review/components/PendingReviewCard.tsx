import OpenInNewOutlined from "@mui/icons-material/OpenInNewOutlined";
import { Alert, Button, Card, CardActions, CardContent, CardHeader, Divider, Stack, Typography } from "@mui/material";
import { useState } from "react";
import { ApiError } from "../../../shared/api/httpClient";
import { ConfirmActionDialog } from "../../../shared/components/ConfirmActionDialog/ConfirmActionDialog";
import { DocumentPreviewThumbnail } from "../../../shared/components/DocumentPreviewThumbnail/DocumentPreviewThumbnail";
import { de } from "../../../shared/i18n/de";
import type { DocumentCategory } from "../../document-categories/api/documentCategoryApi";
import type { Employee } from "../../employees/api/employeeApi";
import { getReviewDocumentFileUrl, type PendingReview } from "../api/documentReviewApi";
import { useConfirmMatch } from "../hooks/useConfirmMatch";
import { CategoryPicker } from "./CategoryPicker";
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
  const [categoryId, setCategoryId] = useState<string | null>(review.suggestedCategoryId);
  const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
  const confirmMutation = useConfirmMatch();
  const fileUrl = getReviewDocumentFileUrl(review.documentId);

  function handleConfirm() {
    if (!selectedEmployeeId) {
      return;
    }
    setConfirmDialogOpen(true);
  }

  function confirmAssignment() {
    if (!selectedEmployeeId) {
      return;
    }
    confirmMutation.mutate({
      documentId: review.documentId,
      employeeId: selectedEmployeeId,
      categoryId,
      newCategoryName: null,
    });
    setConfirmDialogOpen(false);
  }

  const isDisabled = confirmMutation.isPending || confirmMutation.isSuccess;

  return (
    <Card variant="outlined" sx={{ display: "flex", flexDirection: "column", height: "100%" }}>
      <CardHeader
        title={review.originalFilename}
        subheader={review.evidence}
        action={<MatchStatusChip matchStatus={review.matchStatus} />}
        slotProps={{
          title: { sx: { fontWeight: 600, fontSize: "0.95rem" } },
          subheader: { sx: { fontSize: "0.8125rem", mt: 0.25 } },
        }}
        sx={{ pb: 1, alignItems: "flex-start" }}
      />
      <Divider />
      <DocumentPreviewThumbnail fileUrl={fileUrl} filename={review.originalFilename} contentType={review.contentType} />
      <CardContent sx={{ p: 2, flexGrow: 1, "&:last-child": { pb: 2 } }}>
        <Stack spacing={1.25}>
          <Stack direction="row" spacing={1} sx={{ flexWrap: "wrap" }}>
            <ConfidenceChip label={de.review.systemScore} level={review.systemScore} hint={de.review.systemScoreHint} />
            <ConfidenceChip label={de.review.aiConfidence} level={review.llmConfidence} hint={de.review.aiConfidenceHint} />
          </Stack>
          {review.categoryEvidence && (
            <Typography variant="body2" color="text.secondary">
              {de.review.categoryEvidence(review.categoryEvidence)}
            </Typography>
          )}
          <Stack spacing={1}>
            <EmployeePicker
              employees={employees}
              selectedEmployeeId={selectedEmployeeId}
              onChange={setSelectedEmployeeId}
              disabled={isDisabled}
            />
            <CategoryPicker categories={categories} categoryId={categoryId} onChange={setCategoryId} disabled={isDisabled} />
          </Stack>
          {confirmMutation.isError && (
            <Alert severity="error" sx={{ py: 0.5 }}>
              {confirmMutation.error instanceof ApiError
                ? confirmMutation.error.message
                : de.review.confirmationError}
            </Alert>
          )}
          {confirmMutation.isSuccess && (
            <Alert severity="success" sx={{ py: 0.5 }}>
              {de.review.confirmationSuccess}
            </Alert>
          )}
        </Stack>
      </CardContent>
      <CardActions sx={{ px: 2, pb: 2, pt: 0 }}>
        <Button variant="contained" size="small" onClick={handleConfirm} disabled={!selectedEmployeeId || isDisabled}>
          {de.common.actions.confirm}
        </Button>
        <Button
          component="a"
          href={fileUrl}
          target="_blank"
          rel="noopener noreferrer"
          size="small"
          endIcon={<OpenInNewOutlined />}
          aria-label={de.documents.openFor(review.originalFilename)}
        >
          {de.documents.open}
        </Button>
      </CardActions>
      <ConfirmActionDialog
        open={confirmDialogOpen}
        title={de.review.confirmationConfirmTitle}
        message={de.review.confirmationConfirmMessage(review.originalFilename)}
        confirmDisabled={confirmMutation.isPending}
        onCancel={() => setConfirmDialogOpen(false)}
        onConfirm={confirmAssignment}
      />
    </Card>
  );
}
