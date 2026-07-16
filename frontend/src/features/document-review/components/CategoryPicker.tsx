import { Autocomplete, TextField } from "@mui/material";
import type { DocumentCategory } from "../../document-categories/api/documentCategoryApi";
import { de } from "../../../shared/i18n/de";

interface CategoryPickerProps {
  readonly categories: readonly DocumentCategory[];
  readonly categoryId: string | null;
  readonly onChange: (categoryId: string | null) => void;
  readonly disabled?: boolean;
}

export function CategoryPicker({ categories, categoryId, onChange, disabled = false }: CategoryPickerProps) {
  const selectedCategory = categories.find((category) => category.id === categoryId) ?? null;

  return (
    <Autocomplete
      size="small"
      options={categories}
      value={selectedCategory}
      onChange={(_event, value) => onChange(value?.id ?? null)}
      getOptionLabel={(category) => category.displayName}
      isOptionEqualToValue={(option, value) => option.id === value.id}
      disabled={disabled}
      renderInput={(params) => <TextField {...params} label={de.review.existingCategory} size="small" />}
    />
  );
}
