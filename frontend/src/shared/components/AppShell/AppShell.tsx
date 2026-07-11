import { Box, Toolbar, useTheme } from "@mui/material";
import useMediaQuery from "@mui/material/useMediaQuery";
import { useState, type PropsWithChildren } from "react";
import { AppTopBar } from "../AppTopBar/AppTopBar";
import { NavigationDrawer, DRAWER_WIDTH } from "../NavigationDrawer/NavigationDrawer";
import { UploadDialog } from "../../../features/document-upload/components/UploadDialog";

export function AppShell({ children }: PropsWithChildren) {
  const theme = useTheme();
  const isDesktop = useMediaQuery(theme.breakpoints.up("md"));
  const [mobileNavOpen, setMobileNavOpen] = useState(false);
  const [uploadDialogOpen, setUploadDialogOpen] = useState(false);

  return (
    <Box sx={{ display: "flex" }}>
      <AppTopBar
        onMenuClick={() => setMobileNavOpen(true)}
        onUploadClick={() => setUploadDialogOpen(true)}
      />
      <NavigationDrawer
        variant={isDesktop ? "permanent" : "temporary"}
        open={isDesktop || mobileNavOpen}
        onClose={() => setMobileNavOpen(false)}
      />
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          minWidth: 0,
          ml: { md: `${DRAWER_WIDTH}px` },
        }}
      >
        <Toolbar />
        {children}
      </Box>
      <UploadDialog open={uploadDialogOpen} onClose={() => setUploadDialogOpen(false)} />
    </Box>
  );
}
