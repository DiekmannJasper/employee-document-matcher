import ErrorOutline from "@mui/icons-material/ErrorOutlined";
import { Box, Button, Typography } from "@mui/material";

interface ErrorStateProps {
  readonly message?: string;
  readonly onRetry?: () => void;
}

export function ErrorState({ message = "Daten konnten nicht geladen werden.", onRetry }: ErrorStateProps) {
  return (
    <Box role="alert" sx={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 2, py: 6 }}>
      <ErrorOutline color="error" fontSize="large" />
      <Typography color="text.secondary">{message}</Typography>
      {onRetry && (
        <Button variant="outlined" onClick={onRetry}>
          Erneut versuchen
        </Button>
      )}
    </Box>
  );
}
