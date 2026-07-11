import { Chip } from "@mui/material";
import type { ConfidenceLevel } from "../api/documentReviewApi";

const LABELS: Record<ConfidenceLevel, string> = {
  HIGH: "Hoch",
  MEDIUM: "Mittel",
  LOW: "Niedrig",
  NONE: "Kein Signal",
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
