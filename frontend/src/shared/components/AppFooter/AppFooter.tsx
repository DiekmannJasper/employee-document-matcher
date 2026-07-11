import ArrowBack from "@mui/icons-material/ArrowBack";
import { Box, Button } from "@mui/material";
import { useNavigate } from "react-router-dom";

interface AppFooterProps {
  readonly offsetLeft: number;
}

export function AppFooter({ offsetLeft }: AppFooterProps) {
  const navigate = useNavigate();

  return (
    <Box
      component="footer"
      sx={{
        position: "fixed",
        bottom: 0,
        left: { xs: 0, md: offsetLeft },
        right: 0,
        display: "flex",
        alignItems: "center",
        px: 2,
        py: 1,
        borderTop: 1,
        borderColor: "divider",
        backgroundColor: "background.paper",
        transition: (theme) => theme.transitions.create("left"),
      }}
    >
      <Button startIcon={<ArrowBack />} onClick={() => navigate(-1)}>
        Zurück
      </Button>
    </Box>
  );
}
