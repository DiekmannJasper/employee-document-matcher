import { createContext } from "react";

export interface SearchContextValue {
  readonly query: string;
  readonly setQuery: (query: string) => void;
}

export const SearchContext = createContext<SearchContextValue | null>(null);
