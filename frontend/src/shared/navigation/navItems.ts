import type { ComponentType } from "react";
import FactCheck from "@mui/icons-material/FactCheck";
import PeopleAltOutlined from "@mui/icons-material/PeopleAltOutlined";

export interface NavItem {
  readonly id: string;
  readonly label: string;
  readonly to: string;
  readonly icon: ComponentType;
}

export const navItems: readonly NavItem[] = [
  { id: "dashboard", label: "Mitarbeiter", to: "/", icon: PeopleAltOutlined },
  { id: "reviews", label: "Prüffälle", to: "/reviews", icon: FactCheck },
];
