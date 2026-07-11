import MenuIcon from "@mui/icons-material/Menu";
import { AppBar, Box, IconButton, Toolbar, Typography } from "@mui/material";
import { GlobalSearchField } from "../GlobalSearchField/GlobalSearchField";
import { GlobalUploadButton } from "../GlobalUploadButton/GlobalUploadButton";

interface AppTopBarProps {
  readonly onMenuClick: () => void;
  readonly onUploadClick: () => void;
}

export function AppTopBar({ onMenuClick, onUploadClick }: AppTopBarProps) {
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
        <IconButton color="inherit" aria-label="Navigation umschalten" onClick={onMenuClick}>
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
