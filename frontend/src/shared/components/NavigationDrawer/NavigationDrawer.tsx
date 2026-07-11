import { Box, Drawer, Toolbar } from "@mui/material";
import { NavigationList } from "../NavigationList/NavigationList";
import { navItems } from "../../navigation/navItems";

export const DRAWER_WIDTH = 240;
export const MINI_DRAWER_WIDTH = 72;

interface NavigationDrawerProps {
  readonly variant: "permanent" | "temporary";
  readonly open: boolean;
  readonly width: number;
  readonly collapsed: boolean;
  readonly onClose: () => void;
}

export function NavigationDrawer({ variant, open, width, collapsed, onClose }: NavigationDrawerProps) {
  return (
    <Drawer
      variant={variant}
      open={open}
      onClose={onClose}
      ModalProps={{ keepMounted: true }}
      sx={{
        width,
        flexShrink: 0,
        whiteSpace: "nowrap",
        transition: (theme) => theme.transitions.create("width"),
        "& .MuiDrawer-paper": {
          width,
          boxSizing: "border-box",
          overflowX: "hidden",
          transition: (theme) => theme.transitions.create("width"),
        },
      }}
    >
      <Toolbar />
      <Box role="navigation" aria-label="Hauptnavigation">
        <NavigationList
          items={navItems}
          collapsed={collapsed}
          onNavigate={variant === "temporary" ? onClose : undefined}
        />
      </Box>
    </Drawer>
  );
}
