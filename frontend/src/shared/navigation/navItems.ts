import type { ComponentType } from "react";
import FactCheck from "@mui/icons-material/FactCheck";
import PeopleAltOutlined from "@mui/icons-material/PeopleAltOutlined";
import { de } from "../i18n/de";

export interface NavItem {
  readonly id: string;
  readonly label: string;
  readonly to: string;
  readonly icon: ComponentType;
}

export const navItems: readonly NavItem[] = [
  { id: "dashboard", label: de.navigation.employees, to: "/", icon: PeopleAltOutlined },
  { id: "reviews", label: de.navigation.reviews, to: "/reviews", icon: FactCheck },
];
