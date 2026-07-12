import { useContext } from "react";
import { SearchContext } from "./SearchContext";
import { de } from "../i18n/de";

export function useGlobalSearch() {
  const context = useContext(SearchContext);

  if (!context) {
    throw new Error(de.app.errors.missingSearchProvider);
  }

  return context;
}
