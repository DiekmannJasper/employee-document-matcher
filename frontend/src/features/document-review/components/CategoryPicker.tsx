import { Autocomplete, Stack, TextField } from "@mui/material";
import type { DocumentCategory } from "../../document-categories/api/documentCategoryApi";
import { de } from "../../../shared/i18n/de";

export interface CategorySelection {
  readonly categoryId: string | null;
  readonly newCategoryName: string;
}

interface CategoryPickerProps {
  readonly categories: readonly DocumentCategory[];
  readonly selection: CategorySelection;
  readonly onChange: (selection: CategorySelection) => void;
  readonly disabled?: boolean;
}

export function CategoryPicker({ categories, selection, onChange, disabled = false }: CategoryPickerProps) {
  const selectedCategory = categories.find((category) => category.id === selection.categoryId) ?? null;

  return (
    <Stack spacing={1}>
      <Autocomplete
        options={categories}
        value={selectedCategory}
        onChange={(_event, value) => onChange({ categoryId: value?.id ?? null, newCategoryName: "" })}
        getOptionLabel={(category) => category.displayName}
        isOptionEqualToValue={(option, value) => option.id === value.id}
        disabled={disabled}
        renderInput={(params) => <TextField {...params} label={de.review.existingCategory} size="small" />}
      />
      <TextField
        label={de.review.newCategory}
        size="small"
        value={selection.newCategoryName}
        onChange={(event) => onChange({ categoryId: null, newCategoryName: event.target.value })}
        disabled={disabled || selection.categoryId !== null}
      />
    </Stack>
  );
}
