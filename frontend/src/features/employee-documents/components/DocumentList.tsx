import DriveFileMoveOutlined from "@mui/icons-material/DriveFileMoveOutlined";
import OpenInNewOutlined from "@mui/icons-material/OpenInNewOutlined";
import { Box, Button, Card, CardActions, CardContent, FormControl, InputLabel, MenuItem, Select, Stack, Typography } from "@mui/material";
import type { SelectChangeEvent } from "@mui/material/Select";
import { useState } from "react";
import type { DocumentCategory } from "../../document-categories/api/documentCategoryApi";
import { ConfirmActionDialog } from "../../../shared/components/ConfirmActionDialog/ConfirmActionDialog";
import { DocumentPreviewThumbnail } from "../../../shared/components/DocumentPreviewThumbnail/DocumentPreviewThumbnail";
import { getEmployeeDocumentFileUrl, type DocumentSummary } from "../api/employeeDocumentApi";
import { useUpdateDocumentCategory } from "../hooks/useUpdateDocumentCategory";
import { de } from "../../../shared/i18n/de";

const DATE_FORMAT = new Intl.DateTimeFormat(de.locale, { dateStyle: "medium", timeStyle: "short" });

interface DocumentListProps {
  readonly employeeId: string;
  readonly categories: readonly DocumentCategory[];
  readonly documents: readonly DocumentSummary[];
}

export function DocumentList({ employeeId, categories, documents }: DocumentListProps) {
  return (
    <Box
      sx={{
        display: "grid",
        gap: 2,
        gridTemplateColumns: {
          xs: "1fr",
          sm: "repeat(2, minmax(0, 1fr))",
          lg: "repeat(3, minmax(0, 1fr))",
        },
      }}
    >
      {documents.map((document) => (
        <DocumentTile key={document.id} document={document} employeeId={employeeId} categories={categories} />
      ))}
    </Box>
  );
}

interface DocumentTileProps {
  readonly document: DocumentSummary;
  readonly employeeId: string;
  readonly categories: readonly DocumentCategory[];
}

function DocumentTile({ document, employeeId, categories }: DocumentTileProps) {
  const fileUrl = getEmployeeDocumentFileUrl(employeeId, document.id);
  const updateCategory = useUpdateDocumentCategory();
  const [pendingMove, setPendingMove] = useState<{ categoryId: string | null; categoryName: string } | null>(null);

  function handleCategoryChange(event: SelectChangeEvent) {
    const nextCategoryId = event.target.value === "unassigned" ? null : event.target.value;
    const nextCategory = categories.find((category) => category.id === nextCategoryId);
    const nextCategoryName = nextCategory?.displayName ?? de.documents.unassigned;

    if ((document.categoryId ?? null) === nextCategoryId) {
      return;
    }

    setPendingMove({ categoryId: nextCategoryId, categoryName: nextCategoryName });
  }

  function confirmMove() {
    if (!pendingMove) {
      return;
    }

    updateCategory.mutate({
      employeeId,
      documentId: document.id,
      categoryId: pendingMove.categoryId,
      categoryName: pendingMove.categoryId ? pendingMove.categoryName : null,
    });
    setPendingMove(null);
  }

  return (
    <Card
      variant="outlined"
      sx={{
        display: "flex",
        minWidth: 0,
        height: "100%",
        flexDirection: "column",
        overflow: "hidden",
      }}
    >
      <DocumentPreviewThumbnail
        fileUrl={fileUrl}
        filename={document.originalFilename}
        contentType={document.contentType}
      />

      <CardContent sx={{ flexGrow: 1, minWidth: 0, pb: 1 }}>
        <Typography variant="subtitle1" component="h3" noWrap title={document.originalFilename}>
          {document.originalFilename}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {de.documents.uploadedAt(DATE_FORMAT.format(new Date(document.uploadedAt)))}
        </Typography>
        <FormControl fullWidth size="small" sx={{ mt: 2 }}>
          <InputLabel id={`document-category-${document.id}`}>{de.documents.folder}</InputLabel>
          <Select
            labelId={`document-category-${document.id}`}
            label={de.documents.folder}
            value={document.categoryId ?? "unassigned"}
            onChange={handleCategoryChange}
            disabled={updateCategory.isPending}
            startAdornment={<DriveFileMoveOutlined fontSize="small" sx={{ mr: 1, color: "text.secondary" }} />}
          >
            <MenuItem value="unassigned">{de.documents.unassigned}</MenuItem>
            {categories.map((category) => (
              <MenuItem key={category.id} value={category.id}>
                {category.displayName}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </CardContent>

      <CardActions sx={{ px: 2, pb: 2, pt: 0 }}>
        <Stack direction="row" spacing={1} sx={{ width: "100%", justifyContent: "flex-end" }}>
          <Button
            component="a"
            href={fileUrl}
            target="_blank"
            rel="noopener noreferrer"
            size="small"
            endIcon={<OpenInNewOutlined />}
            aria-label={de.documents.openFor(document.originalFilename)}
          >
            {de.documents.open}
          </Button>
        </Stack>
      </CardActions>
      <ConfirmActionDialog
        open={pendingMove !== null}
        title={de.documents.moveConfirmTitle}
        message={de.documents.moveConfirmMessage(document.originalFilename, pendingMove?.categoryName ?? "")}
        confirmLabel={de.common.actions.move}
        onCancel={() => setPendingMove(null)}
        onConfirm={confirmMove}
      />
    </Card>
  );
}
