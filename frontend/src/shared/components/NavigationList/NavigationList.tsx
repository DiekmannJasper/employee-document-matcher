import { List, ListItemButton, ListItemIcon, ListItemText } from "@mui/material";
import { NavLink } from "react-router-dom";
import type { NavItem } from "../../navigation/navItems";

interface NavigationListProps {
  readonly items: readonly NavItem[];
  readonly onNavigate?: () => void;
}

export function NavigationList({ items, onNavigate }: NavigationListProps) {
  return (
    <List>
      {items.map((item) => (
        <ListItemButton
          key={item.id}
          component={NavLink}
          to={item.to}
          onClick={onNavigate}
          sx={{
            "&.active": {
              backgroundColor: "action.selected",
              fontWeight: 600,
            },
          }}
        >
          <ListItemIcon>
            <item.icon />
          </ListItemIcon>
          <ListItemText primary={item.label} />
        </ListItemButton>
      ))}
    </List>
  );
}
