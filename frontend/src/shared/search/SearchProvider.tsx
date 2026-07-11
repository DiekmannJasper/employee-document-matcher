import { useMemo, useState, type PropsWithChildren } from "react";
import { SearchContext } from "./SearchContext";

export function SearchProvider({ children }: PropsWithChildren) {
  const [query, setQuery] = useState("");
  const value = useMemo(() => ({ query, setQuery }), [query]);

  return <SearchContext.Provider value={value}>{children}</SearchContext.Provider>;
}
