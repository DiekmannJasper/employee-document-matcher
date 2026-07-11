import { Box, Toolbar, useTheme } from "@mui/material";
import useMediaQuery from "@mui/material/useMediaQuery";
import { useState, type PropsWithChildren } from "react";
import { useLocation } from "react-router-dom";
import { AppFooter } from "../AppFooter/AppFooter";
import { AppTopBar } from "../AppTopBar/AppTopBar";
import { NavigationDrawer, DRAWER_WIDTH } from "../NavigationDrawer/NavigationDrawer";
import { UploadDialog } from "../../../features/document-upload/components/UploadDialog";
import { SearchProvider } from "../../search/SearchProvider";

export function AppShell({ children }: PropsWithChildren) {
  const theme = useTheme();
  const location = useLocation();
  const isDesktop = useMediaQuery(theme.breakpoints.up("md"));
  const [mobileNavOpen, setMobileNavOpen] = useState(false);
  const [desktopNavOpen, setDesktopNavOpen] = useState(true);
  const [uploadDialogOpen, setUploadDialogOpen] = useState(false);

  const navOpen = isDesktop ? desktopNavOpen : mobileNavOpen;
  const toggleNav = () => (isDesktop ? setDesktopNavOpen((open) => !open) : setMobileNavOpen((open) => !open));
  const mainOffsetLeft = isDesktop && desktopNavOpen ? DRAWER_WIDTH : 0;
  const showFooter = location.pathname !== "/";

  return (
    <SearchProvider>
      <Box sx={{ display: "flex" }}>
        <AppTopBar onMenuClick={toggleNav} onUploadClick={() => setUploadDialogOpen(true)} />
        <NavigationDrawer
          variant={isDesktop ? "persistent" : "temporary"}
          open={navOpen}
          onClose={() => setMobileNavOpen(false)}
        />
        <Box
          component="main"
          sx={{
            flexGrow: 1,
            minWidth: 0,
            ml: { md: `${mainOffsetLeft}px` },
            pb: showFooter ? 8 : 0,
            transition: theme.transitions.create("margin-left"),
          }}
        >
          <Toolbar />
          {children}
        </Box>
        {showFooter && <AppFooter offsetLeft={mainOffsetLeft} />}
      </Box>
      <UploadDialog open={uploadDialogOpen} onClose={() => setUploadDialogOpen(false)} />
    </SearchProvider>
  );
}
