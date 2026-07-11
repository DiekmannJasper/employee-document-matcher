import { List, ListItemButton, ListItemIcon, ListItemText, Tooltip } from "@mui/material";
import { NavLink } from "react-router-dom";
import type { NavItem } from "../../navigation/navItems";

interface NavigationListProps {
  readonly items: readonly NavItem[];
  readonly collapsed?: boolean;
  readonly onNavigate?: () => void;
}

export function NavigationList({ items, collapsed = false, onNavigate }: NavigationListProps) {
  return (
    <List>
      {items.map((item) => (
        <Tooltip key={item.id} title={collapsed ? item.label : ""} placement="right">
          <ListItemButton
            component={NavLink}
            to={item.to}
            onClick={onNavigate}
            sx={{
              justifyContent: collapsed ? "center" : "flex-start",
              minHeight: 48,
              px: 2.5,
              "&.active": {
                backgroundColor: "action.selected",
                fontWeight: 600,
              },
            }}
          >
            <ListItemIcon sx={{ minWidth: 0, mr: collapsed ? 0 : 2, justifyContent: "center" }}>
              <item.icon />
            </ListItemIcon>
            {!collapsed && <ListItemText primary={item.label} />}
          </ListItemButton>
        </Tooltip>
      ))}
    </List>
  );
}
