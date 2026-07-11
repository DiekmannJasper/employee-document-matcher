import { useContext } from "react";
import { SearchContext } from "./SearchContext";

export function useGlobalSearch() {
  const context = useContext(SearchContext);

  if (!context) {
    throw new Error("useGlobalSearch must be used within a SearchProvider");
  }

  return context;
}
