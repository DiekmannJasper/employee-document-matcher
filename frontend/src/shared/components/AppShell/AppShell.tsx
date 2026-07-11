import { Box, Toolbar, useTheme } from "@mui/material";
import useMediaQuery from "@mui/material/useMediaQuery";
import { useState, type PropsWithChildren } from "react";
import { AppFooter } from "../AppFooter/AppFooter";
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

  const navExpanded = isDesktop ? desktopNavExpanded : mobileNavOpen;
  const toggleNav = () =>
    isDesktop ? setDesktopNavExpanded((expanded) => !expanded) : setMobileNavOpen((open) => !open);

  const desktopDrawerWidth = desktopNavExpanded ? DRAWER_WIDTH : MINI_DRAWER_WIDTH;
  const mainOffsetLeft = isDesktop ? desktopDrawerWidth : 0;

  return (
    <SearchProvider>
      <Box sx={{ display: "flex" }}>
        <AppTopBar
          navExpanded={navExpanded}
          onToggleNavigation={toggleNav}
          onUploadClick={() => setUploadDialogOpen(true)}
        />
        <NavigationDrawer
          variant={isDesktop ? "permanent" : "temporary"}
          open={isDesktop || mobileNavOpen}
          width={isDesktop ? desktopDrawerWidth : DRAWER_WIDTH}
          collapsed={isDesktop && !desktopNavExpanded}
          onClose={() => setMobileNavOpen(false)}
        />
        <Box
          component="main"
          sx={{
            flexGrow: 1,
            minWidth: 0,
            ml: { md: `${mainOffsetLeft}px` },
            pb: 8,
            transition: theme.transitions.create("margin-left"),
          }}
        >
          <Toolbar />
          {children}
        </Box>
        <AppFooter offsetLeft={mainOffsetLeft} />
      </Box>
      <UploadDialog open={uploadDialogOpen} onClose={() => setUploadDialogOpen(false)} />
    </SearchProvider>
  );
}
