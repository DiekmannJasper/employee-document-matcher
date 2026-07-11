import { Alert, Button, Card, CardActions, CardContent, Stack, Typography } from "@mui/material";
import { useState } from "react";
import { ApiError } from "../../../shared/api/httpClient";
import type { Employee } from "../../employees/api/employeeApi";
import type { PendingReview } from "../api/documentReviewApi";
import { useConfirmMatch } from "../hooks/useConfirmMatch";
import { EmployeePicker } from "./EmployeePicker";
import { MatchStatusChip } from "./MatchStatusChip";

interface PendingReviewCardProps {
  readonly review: PendingReview;
  readonly employees: readonly Employee[];
}

export function PendingReviewCard({ review, employees }: PendingReviewCardProps) {
  const [selectedEmployeeId, setSelectedEmployeeId] = useState<string | null>(review.suggestedEmployeeId);
  const confirmMutation = useConfirmMatch();

  function handleConfirm() {
    if (!selectedEmployeeId) {
      return;
    }
    confirmMutation.mutate({ documentId: review.documentId, employeeId: selectedEmployeeId });
  }

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
          <EmployeePicker
            employees={employees}
            selectedEmployeeId={selectedEmployeeId}
            onChange={setSelectedEmployeeId}
            disabled={confirmMutation.isPending || confirmMutation.isSuccess}
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
        <Button
          variant="contained"
          onClick={handleConfirm}
          disabled={!selectedEmployeeId || confirmMutation.isPending || confirmMutation.isSuccess}
        >
          Bestätigen
        </Button>
      </CardActions>
    </Card>
  );
}
