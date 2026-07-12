import { Chip } from "@mui/material";
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
}

/**
 * Shows a qualitative band only (never a percentage) - the value is not a calibrated
 * probability, just a coarse, backend-thresholded signal.
 */
export function ConfidenceChip({ label, level }: ConfidenceChipProps) {
  return <Chip size="small" variant="outlined" color={COLORS[level]} label={`${label}: ${LABELS[level]}`} />;
}
