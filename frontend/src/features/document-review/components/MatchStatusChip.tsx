import { Chip } from "@mui/material";
import type { MatchStatus } from "../api/documentReviewApi";
import { de } from "../../../shared/i18n/de";

const LABELS: Record<MatchStatus, string> = {
  MATCHED: de.review.matchStatus.matched,
  AMBIGUOUS: de.review.matchStatus.ambiguous,
  NO_MATCH: de.review.matchStatus.noMatch,
  UNREADABLE: de.review.matchStatus.unreadable,
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
