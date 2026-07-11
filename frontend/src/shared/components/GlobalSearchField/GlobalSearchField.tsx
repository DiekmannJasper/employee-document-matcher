import SearchIcon from "@mui/icons-material/Search";
import { InputAdornment, TextField } from "@mui/material";
import { useGlobalSearch } from "../../search/useGlobalSearch";

export function GlobalSearchField() {
  const { query, setQuery } = useGlobalSearch();

  return (
    <TextField
      size="small"
      placeholder="Mitarbeiter suchen…"
      value={query}
      onChange={(event) => setQuery(event.target.value)}
      slotProps={{
        input: {
          startAdornment: (
            <InputAdornment position="start">
              <SearchIcon fontSize="small" />
            </InputAdornment>
          ),
        },
      }}
      sx={{ width: { xs: 140, sm: 240, md: 320 } }}
    />
  );
}
