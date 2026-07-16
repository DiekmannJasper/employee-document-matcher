import MenuIcon from "@mui/icons-material/Menu";
import { AppBar, Box, IconButton, Toolbar, Typography } from "@mui/material";
import { GlobalExternalImportButton } from "../GlobalExternalImportButton/GlobalExternalImportButton";
import { GlobalSearchField } from "../GlobalSearchField/GlobalSearchField";
import { GlobalUploadButton } from "../GlobalUploadButton/GlobalUploadButton";
import { de } from "../../i18n/de";

interface AppTopBarProps {
  readonly mobileNavOpen: boolean;
  readonly onToggleMobileNavigation: () => void;
  readonly onUploadClick: () => void;
  readonly onExternalImportClick: () => void;
  readonly showSearch: boolean;
}

export function AppTopBar({
  mobileNavOpen,
  onToggleMobileNavigation,
  onUploadClick,
  onExternalImportClick,
  showSearch,
}: AppTopBarProps) {
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
          aria-label={mobileNavOpen ? de.navigation.close : de.navigation.open}
          onClick={onToggleMobileNavigation}
          sx={{ display: { md: "none" } }}
        >
          <MenuIcon />
        </IconButton>
        <Typography component="h1" variant="h6" noWrap sx={{ display: { xs: "none", sm: "block" } }}>
          {de.app.title}
        </Typography>
        <Box sx={{ flexGrow: 1 }} />
        {showSearch && <GlobalSearchField />}
        <GlobalExternalImportButton onClick={onExternalImportClick} />
        <GlobalUploadButton onClick={onUploadClick} />
      </Toolbar>
    </AppBar>
  );
}
