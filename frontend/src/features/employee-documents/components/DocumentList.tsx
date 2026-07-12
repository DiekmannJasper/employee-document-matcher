import DescriptionOutlined from "@mui/icons-material/DescriptionOutlined";
import { List, ListItem, ListItemIcon, ListItemText } from "@mui/material";
import type { DocumentSummary } from "../api/employeeDocumentApi";
import { de } from "../../../shared/i18n/de";

const DATE_FORMAT = new Intl.DateTimeFormat(de.locale, { dateStyle: "medium", timeStyle: "short" });

interface DocumentListProps {
  readonly documents: readonly DocumentSummary[];
}

export function DocumentList({ documents }: DocumentListProps) {
  return (
    <List>
      {documents.map((document) => (
        <ListItem key={document.id} divider>
          <ListItemIcon>
            <DescriptionOutlined />
          </ListItemIcon>
          <ListItemText
            primary={document.originalFilename}
            secondary={de.documents.uploadedAt(DATE_FORMAT.format(new Date(document.uploadedAt)))}
          />
        </ListItem>
      ))}
    </List>
  );
}
