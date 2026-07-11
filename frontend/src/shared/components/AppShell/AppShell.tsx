import { Box } from "@mui/material";
import useMediaQuery from "@mui/material/useMediaQuery";
import { useTheme } from "@mui/material/styles";
import { useState, type PropsWithChildren } from "react";
import { AppTopBar } from "../AppTopBar/AppTopBar";
import { NavigationDrawer, DRAWER_WIDTH, MINI_DRAWER_WIDTH } from "../NavigationDrawer/NavigationDrawer";
import { UploadDialog } from "../../../features/document-upload/components/UploadDialog";
import { SearchProvider } from "../../search/SearchProvider";

export function AppShell({ children }: PropsWithChildren) {
  const theme = useTheme();
  const isDesktop = useMediaQuery(theme.breakpoints.up("md"));
  const [mobileNavOpen, setMobileNavOpen] = useState(false);
  const [desktopNavExpanded, setDesktopNavExpanded] = useState(true);
  const [uploadDialogOpen, setUploadDialogOpen] = useState(false);

  const desktopDrawerWidth = desktopNavExpanded ? DRAWER_WIDTH : MINI_DRAWER_WIDTH;

  return (
    <SearchProvider>
      <Box sx={{ display: "flex", height: "100vh", overflow: "hidden" }}>
        <NavigationDrawer
          variant={isDesktop ? "permanent" : "temporary"}
          open={isDesktop || mobileNavOpen}
          width={isDesktop ? desktopDrawerWidth : DRAWER_WIDTH}
          collapsed={isDesktop && !desktopNavExpanded}
          onClose={() => setMobileNavOpen(false)}
          onToggle={() => setDesktopNavExpanded((expanded) => !expanded)}
        />
        <Box sx={{ display: "flex", flexDirection: "column", flexGrow: 1, minWidth: 0, height: "100vh" }}>
          <AppTopBar
            mobileNavOpen={mobileNavOpen}
            onToggleMobileNavigation={() => setMobileNavOpen((open) => !open)}
            onUploadClick={() => setUploadDialogOpen(true)}
          />
          <Box component="main" sx={{ flexGrow: 1, minHeight: 0, overflowY: "auto" }}>
            {children}
          </Box>
        </Box>
      </Box>
      <UploadDialog open={uploadDialogOpen} onClose={() => setUploadDialogOpen(false)} />
    </SearchProvider>
  );
}
