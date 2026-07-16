import { Tab, Tabs } from "@mui/material";
import { de } from "../../../shared/i18n/de";

export interface CategoryFolder {
  readonly id: string;
  readonly label: string;
  readonly count: number;
}

interface CategoryFolderTabsProps {
  readonly folders: readonly CategoryFolder[];
  readonly selectedId: string;
  readonly onSelect: (id: string) => void;
}

export function CategoryFolderTabs({ folders, selectedId, onSelect }: CategoryFolderTabsProps) {
  return (
    <Tabs
      value={selectedId}
      onChange={(_event, value: string) => onSelect(value)}
      variant="scrollable"
      allowScrollButtonsMobile
      aria-label={de.documents.categoriesAriaLabel}
    >
      {folders.map((folder) => (
        <Tab key={folder.id} value={folder.id} label={`${folder.label} (${folder.count})`} />
      ))}
    </Tabs>
  );
}
