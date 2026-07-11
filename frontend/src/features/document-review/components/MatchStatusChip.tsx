import { Chip } from "@mui/material";
import type { MatchStatus } from "../api/documentReviewApi";

const LABELS: Record<MatchStatus, string> = {
  MATCHED: "Eindeutiger Treffer",
  AMBIGUOUS: "Mehrdeutig",
  NO_MATCH: "Kein Treffer",
};

const COLORS: Record<MatchStatus, "success" | "warning" | "default"> = {
  MATCHED: "success",
  AMBIGUOUS: "warning",
  NO_MATCH: "default",
};

interface MatchStatusChipProps {
  readonly matchStatus: MatchStatus;
}

export function MatchStatusChip({ matchStatus }: MatchStatusChipProps) {
  return <Chip size="small" label={LABELS[matchStatus]} color={COLORS[matchStatus]} />;
}
