import InboxOutlined from "@mui/icons-material/InboxOutlined";
import { Box, Typography } from "@mui/material";

interface EmptyStateProps {
  readonly message: string;
}

export function EmptyState({ message }: EmptyStateProps) {
  return (
    <Box sx={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 2, py: 6 }}>
      <InboxOutlined color="disabled" fontSize="large" />
      <Typography color="text.secondary">{message}</Typography>
    </Box>
  );
}
