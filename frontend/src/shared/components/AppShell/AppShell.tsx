import { Box } from "@mui/material";
import useMediaQuery from "@mui/material/useMediaQuery";
import { useTheme } from "@mui/material/styles";
import { useState, type PropsWithChildren } from "react";
import { useLocation } from "react-router-dom";
import { AppFooter } from "../AppFooter/AppFooter";
import { AppTopBar } from "../AppTopBar/AppTopBar";
import { NavigationDrawer } from "../NavigationDrawer/NavigationDrawer";
import { ExternalImportDialog } from "../../../features/document-upload/components/ExternalImportDialog";
import { UploadDialog } from "../../../features/document-upload/components/UploadDialog";
import { SearchProvider } from "../../search/SearchProvider";

export function AppShell({ children }: PropsWithChildren) {
  const theme = useTheme();
  const location = useLocation();
  const isDesktop = useMediaQuery(theme.breakpoints.up("md"));
  const [mobileNavOpen, setMobileNavOpen] = useState(false);
  const [uploadDialogOpen, setUploadDialogOpen] = useState(false);
  const [externalImportDialogOpen, setExternalImportDialogOpen] = useState(false);
  // Incremented on every open and used as the dialog's key, so each upload
  // session starts with fresh state (file selection, mutation result).
  const [uploadSession, setUploadSession] = useState(0);

  function openUploadDialog() {
    setUploadSession((session) => session + 1);
    setUploadDialogOpen(true);
  }

  return (
    <SearchProvider>
      <Box sx={{ display: "flex", height: "100vh", overflow: "hidden" }}>
        <NavigationDrawer
          variant={isDesktop ? "permanent" : "temporary"}
          open={isDesktop || mobileNavOpen}
          onClose={() => setMobileNavOpen(false)}
        />
        <Box sx={{ display: "flex", flexDirection: "column", flexGrow: 1, minWidth: 0, height: "100vh" }}>
          <AppTopBar
            mobileNavOpen={mobileNavOpen}
            onToggleMobileNavigation={() => setMobileNavOpen((open) => !open)}
            onUploadClick={openUploadDialog}
            onExternalImportClick={() => setExternalImportDialogOpen(true)}
            showSearch={location.pathname === "/"}
          />
          <Box component="main" sx={{ flexGrow: 1, minHeight: 0, overflowY: "auto" }}>
            {children}
          </Box>
          <AppFooter />
        </Box>
      </Box>
      <UploadDialog key={uploadSession} open={uploadDialogOpen} onClose={() => setUploadDialogOpen(false)} />
      <ExternalImportDialog open={externalImportDialogOpen} onClose={() => setExternalImportDialogOpen(false)} />
    </SearchProvider>
  );
}
