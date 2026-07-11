import ChevronLeft from "@mui/icons-material/ChevronLeft";
import ChevronRight from "@mui/icons-material/ChevronRight";
import { AppBar, Box, IconButton, Toolbar, Typography } from "@mui/material";
import { GlobalSearchField } from "../GlobalSearchField/GlobalSearchField";
import { GlobalUploadButton } from "../GlobalUploadButton/GlobalUploadButton";

interface AppTopBarProps {
  readonly navExpanded: boolean;
  readonly onToggleNavigation: () => void;
  readonly onUploadClick: () => void;
}

export function AppTopBar({ navExpanded, onToggleNavigation, onUploadClick }: AppTopBarProps) {
  return (
    <AppBar
      position="fixed"
      color="default"
      elevation={0}
      sx={{
        borderBottom: 1,
        borderColor: "divider",
        zIndex: (theme) => theme.zIndex.drawer + 1,
      }}
    >
      <Toolbar sx={{ gap: 2 }}>
        <IconButton
          color="inherit"
          aria-label={navExpanded ? "Navigation verkleinern" : "Navigation vergrößern"}
          onClick={onToggleNavigation}
        >
          {navExpanded ? <ChevronLeft /> : <ChevronRight />}
        </IconButton>
        <Typography component="h1" variant="h6" noWrap sx={{ display: { xs: "none", sm: "block" } }}>
          Employee Document Matcher
        </Typography>
        <Box sx={{ flexGrow: 1 }} />
        <GlobalSearchField />
        <GlobalUploadButton onClick={onUploadClick} />
      </Toolbar>
    </AppBar>
  );
}
