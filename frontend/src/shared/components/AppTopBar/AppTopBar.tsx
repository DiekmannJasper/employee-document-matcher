import MenuIcon from "@mui/icons-material/Menu";
import { AppBar, IconButton, Toolbar, Typography } from "@mui/material";
import { GlobalUploadButton } from "../GlobalUploadButton/GlobalUploadButton";

interface AppTopBarProps {
  readonly onMenuClick: () => void;
  readonly onUploadClick: () => void;
}

export function AppTopBar({ onMenuClick, onUploadClick }: AppTopBarProps) {
  return (
    <AppBar position="fixed" color="default" elevation={0} sx={{ borderBottom: 1, borderColor: "divider" }}>
      <Toolbar sx={{ gap: 2 }}>
        <IconButton
          color="inherit"
          aria-label="Navigation öffnen"
          onClick={onMenuClick}
          sx={{ display: { md: "none" } }}
        >
          <MenuIcon />
        </IconButton>
        <Typography component="h1" variant="h6" sx={{ flexGrow: 1 }}>
          Employee Document Matcher
        </Typography>
        <GlobalUploadButton onClick={onUploadClick} />
      </Toolbar>
    </AppBar>
  );
}
