import ChevronLeft from "@mui/icons-material/ChevronLeft";
import ChevronRight from "@mui/icons-material/ChevronRight";
import { Box, Drawer, IconButton, Toolbar } from "@mui/material";
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
  readonly onToggle: () => void;
}

export function NavigationDrawer({ variant, open, width, collapsed, onClose, onToggle }: NavigationDrawerProps) {
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
      <Box sx={{ display: "flex", flexDirection: "column", height: "100%" }}>
        <Toolbar />
        <Box role="navigation" aria-label="Hauptnavigation" sx={{ flexGrow: 1, overflowY: "auto" }}>
          <NavigationList
            items={navItems}
            collapsed={collapsed}
            onNavigate={variant === "temporary" ? onClose : undefined}
          />
        </Box>
        {variant === "permanent" && (
          <Box
            sx={{
              display: "flex",
              justifyContent: collapsed ? "center" : "flex-end",
              borderTop: 1,
              borderColor: "divider",
              p: 1,
            }}
          >
            <IconButton
              onClick={onToggle}
              aria-label={collapsed ? "Navigation vergrößern" : "Navigation verkleinern"}
            >
              {collapsed ? <ChevronRight /> : <ChevronLeft />}
            </IconButton>
          </Box>
        )}
      </Box>
    </Drawer>
  );
}
