import { Chip, Tooltip } from "@mui/material";
import type { ConfidenceLevel } from "../api/documentReviewApi";
import { de } from "../../../shared/i18n/de";

const LABELS: Record<ConfidenceLevel, string> = {
  HIGH: de.review.confidence.high,
  MEDIUM: de.review.confidence.medium,
  LOW: de.review.confidence.low,
  NONE: de.review.confidence.none,
};

const COLORS: Record<ConfidenceLevel, "success" | "warning" | "error" | "default"> = {
  HIGH: "success",
  MEDIUM: "warning",
  LOW: "error",
  NONE: "default",
};

interface ConfidenceChipProps {
  readonly label: string;
  readonly level: ConfidenceLevel;
  readonly hint?: string;
}

/**
 * Shows a qualitative band only (never a percentage) - the value is not a calibrated
 * probability, just a coarse, backend-thresholded signal.
 */
export function ConfidenceChip({ label, level, hint }: ConfidenceChipProps) {
  const chip = <Chip size="small" variant="outlined" color={COLORS[level]} label={`${label}: ${LABELS[level]}`} />;
  return hint ? (
    <Tooltip title={hint} arrow>
      {chip}
    </Tooltip>
  ) : (
    chip
  );
}
