import { Chip } from "@mui/material";
import type { MatchStatus } from "../api/documentReviewApi";

const LABELS: Record<MatchStatus, string> = {
  MATCHED: "Eindeutiger Treffer",
  AMBIGUOUS: "Mehrdeutig",
  NO_MATCH: "Kein Treffer",
  UNREADABLE: "Nicht lesbar",
};

const COLORS: Record<MatchStatus, "success" | "warning" | "error" | "default"> = {
  MATCHED: "success",
  AMBIGUOUS: "warning",
  NO_MATCH: "default",
  UNREADABLE: "error",
};

interface MatchStatusChipProps {
  readonly matchStatus: MatchStatus;
}

export function MatchStatusChip({ matchStatus }: MatchStatusChipProps) {
  return <Chip size="small" label={LABELS[matchStatus]} color={COLORS[matchStatus]} />;
}
