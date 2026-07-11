import MenuIcon from "@mui/icons-material/Menu";
import { AppBar, Box, IconButton, Toolbar, Typography } from "@mui/material";
import { GlobalSearchField } from "../GlobalSearchField/GlobalSearchField";
import { GlobalUploadButton } from "../GlobalUploadButton/GlobalUploadButton";

interface AppTopBarProps {
  readonly mobileNavOpen: boolean;
  readonly onToggleMobileNavigation: () => void;
  readonly onUploadClick: () => void;
}

export function AppTopBar({ mobileNavOpen, onToggleMobileNavigation, onUploadClick }: AppTopBarProps) {
  return (
    <AppBar
      position="static"
      color="default"
      elevation={0}
      sx={{
        flexShrink: 0,
        borderBottom: 1,
        borderColor: "divider",
        backgroundColor: "background.paper",
      }}
    >
      <Toolbar sx={{ gap: 2 }}>
        <IconButton
          color="inherit"
          aria-label={mobileNavOpen ? "Navigation schließen" : "Navigation öffnen"}
          onClick={onToggleMobileNavigation}
          sx={{ display: { md: "none" } }}
        >
          <MenuIcon />
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
