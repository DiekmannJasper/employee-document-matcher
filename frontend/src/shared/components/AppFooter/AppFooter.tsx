import ArrowBack from "@mui/icons-material/ArrowBack";
import { Box, Button } from "@mui/material";
import { useLocation, useNavigate } from "react-router-dom";

export function AppFooter() {
  const navigate = useNavigate();
  const location = useLocation();
  const canGoBack = location.pathname !== "/";

  return (
    <Box
      component="footer"
      sx={{
        flexShrink: 0,
        display: "flex",
        alignItems: "center",
        px: 2,
        py: 1,
        borderTop: 1,
        borderColor: "divider",
        backgroundColor: "background.paper",
      }}
    >
      <Button startIcon={<ArrowBack />} disabled={!canGoBack} onClick={() => navigate(-1)}>
        Zurück
      </Button>
    </Box>
  );
}
