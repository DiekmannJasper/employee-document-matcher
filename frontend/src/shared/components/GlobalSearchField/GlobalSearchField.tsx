import SearchIcon from "@mui/icons-material/Search";
import { InputAdornment, TextField } from "@mui/material";
import { useGlobalSearch } from "../../search/useGlobalSearch";
import { de } from "../../i18n/de";

export function GlobalSearchField() {
  const { query, setQuery } = useGlobalSearch();

  return (
    <TextField
      size="small"
      placeholder={de.employees.searchPlaceholder}
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
