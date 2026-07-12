import { Box, CircularProgress, Typography } from "@mui/material";
import { de } from "../../i18n/de";

interface LoadingStateProps {
  readonly message?: string;
}

export function LoadingState({ message = de.states.loading }: LoadingStateProps) {
  return (
    <Box role="status" sx={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 2, py: 6 }}>
      <CircularProgress size={32} />
      <Typography color="text.secondary">{message}</Typography>
    </Box>
  );
}
