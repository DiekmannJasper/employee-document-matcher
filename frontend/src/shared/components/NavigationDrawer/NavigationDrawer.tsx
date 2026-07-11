import { Box, Drawer, Toolbar } from "@mui/material";
import { NavigationList } from "../NavigationList/NavigationList";
import { navItems } from "../../navigation/navItems";

export const DRAWER_WIDTH = 240;

interface NavigationDrawerProps {
  readonly variant: "permanent" | "temporary";
  readonly open: boolean;
  readonly onClose: () => void;
}

export function NavigationDrawer({ variant, open, onClose }: NavigationDrawerProps) {
  return (
    <Drawer
      variant={variant}
      open={open}
      onClose={onClose}
      ModalProps={{ keepMounted: true }}
      sx={{
        width: DRAWER_WIDTH,
        flexShrink: 0,
        "& .MuiDrawer-paper": { width: DRAWER_WIDTH, boxSizing: "border-box" },
      }}
    >
      <Toolbar />
      <Box role="navigation" aria-label="Hauptnavigation">
        <NavigationList items={navItems} onNavigate={variant === "temporary" ? onClose : undefined} />
      </Box>
    </Drawer>
  );
}
