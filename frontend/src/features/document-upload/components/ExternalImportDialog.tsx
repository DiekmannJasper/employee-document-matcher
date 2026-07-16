import CloseIcon from "@mui/icons-material/Close";
import CloudDownloadOutlined from "@mui/icons-material/CloudDownloadOutlined";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  IconButton,
  InputLabel,
  LinearProgress,
  MenuItem,
  Select,
  Stack,
  Typography,
} from "@mui/material";
import type { SelectChangeEvent } from "@mui/material/Select";
import { useMemo, useState } from "react";
import { de } from "../../../shared/i18n/de";
import { useImportExternalDocument } from "../hooks/useImportExternalDocument";

interface ExternalImportDialogProps {
  readonly open: boolean;
  readonly onClose: () => void;
}

const MOCK_EXTERNAL_SOURCES = [
  {
    id: "datev",
    label: "DATEV",
    documents: [
      {
        id: "datev-salary-laura",
        filename: "datev-gehaltsabrechnung-laura-hoffmann.pdf",
        expectedOutcome: "Auto-Zuordnung zu Laura Hoffmann und Gehalt",
      },
      {
        id: "datev-review-no-employee",
        filename: "datev-gehalt-ohne-mitarbeiter.pdf",
        expectedOutcome: "Prueffall: Kategorie-Signal, aber kein Mitarbeiter-Match",
      },
      {
        id: "datev-zip-payroll-month",
        filename: "datev-export-lohn-januar.zip",
        expectedOutcome: "ZIP-Paket: importiert mehrere PDFs mit Auto-Zuordnung und Prueffall",
      },
    ],
  },
  {
    id: "personio",
    label: "Personio",
    documents: [
      {
        id: "datev-certificate-miriam",
        filename: "personio-bescheinigung-miriam-weber.pdf",
        expectedOutcome: "Auto-Zuordnung zu Miriam Weber und Bescheinigungen",
      },
      {
        id: "datev-zip-payroll-month",
        filename: "personio-dokumentenexport.zip",
        expectedOutcome: "ZIP-Paket: mehrere Dokumente aus einer externen Ablage",
      },
    ],
  },
  {
    id: "successfactors",
    label: "SAP SuccessFactors",
    documents: [
      {
        id: "datev-review-ambiguous",
        filename: "successfactors-vertrag-mehrere-personen.pdf",
        expectedOutcome: "Prueffall: mehrere bekannte Namen",
      },
    ],
  },
  {
    id: "lexware-office",
    label: "Lexware Office",
    documents: [
      {
        id: "datev-salary-laura",
        filename: "lexware-office-gehaltsabrechnung-laura-hoffmann.pdf",
        expectedOutcome: "Auto-Zuordnung zu Laura Hoffmann und Gehalt",
      },
      {
        id: "datev-review-no-employee",
        filename: "lexware-office-gehalt-ohne-mitarbeiter.pdf",
        expectedOutcome: "Prueffall: Kategorie-Signal, aber kein Mitarbeiter-Match",
      },
    ],
  },
  {
    id: "sevdesk",
    label: "sevDesk",
    documents: [
      {
        id: "datev-certificate-miriam",
        filename: "sevdesk-bescheinigung-miriam-weber.pdf",
        expectedOutcome: "Auto-Zuordnung zu Miriam Weber und Bescheinigungen",
      },
    ],
  },
  {
    id: "custom-api",
    label: "Eigene HR API",
    documents: [
      {
        id: "datev-review-ambiguous",
        filename: "hr-api-vertrag-mehrere-personen.pdf",
        expectedOutcome: "Prueffall: mehrere bekannte Namen",
      },
      {
        id: "datev-zip-payroll-month",
        filename: "hr-api-bulk-import.zip",
        expectedOutcome: "ZIP-Paket: Bulk-Import mit gemischten Ergebnissen",
      },
    ],
  },
] as const;

export function ExternalImportDialog({ open, onClose }: ExternalImportDialogProps) {
  const importExternalDocument = useImportExternalDocument();
  const [sourceId, setSourceId] = useState<string>(MOCK_EXTERNAL_SOURCES[0].id);

  const selectedSource = useMemo(
    () => MOCK_EXTERNAL_SOURCES.find((source) => source.id === sourceId) ?? MOCK_EXTERNAL_SOURCES[0],
    [sourceId],
  );
  const [documentId, setDocumentId] = useState<string>(selectedSource.documents[0].id);
  const selectedDocument =
    selectedSource.documents.find((document) => document.id === documentId) ?? selectedSource.documents[0];

  function handleSourceChange(event: SelectChangeEvent) {
    const nextSource = MOCK_EXTERNAL_SOURCES.find((source) => source.id === event.target.value) ?? MOCK_EXTERNAL_SOURCES[0];
    setSourceId(nextSource.id);
    setDocumentId(nextSource.documents[0].id);
  }

  function handleDialogClose() {
    if (importExternalDocument.isPending) {
      return;
    }
    onClose();
  }

  return (
    <Dialog open={open} onClose={handleDialogClose} fullWidth maxWidth="sm">
      <DialogTitle sx={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
        {de.externalDocuments.title}
        <IconButton aria-label={de.common.actions.close} onClick={handleDialogClose} size="small">
          <CloseIcon fontSize="small" />
        </IconButton>
      </DialogTitle>
      <DialogContent>
        <Stack spacing={1.5}>
          <Typography variant="body2" color="text.secondary">
            {de.externalDocuments.description}
          </Typography>

          <FormControl fullWidth size="small">
            <InputLabel id="external-source-label">{de.externalDocuments.sourceLabel}</InputLabel>
            <Select
              labelId="external-source-label"
              label={de.externalDocuments.sourceLabel}
              value={sourceId}
              onChange={handleSourceChange}
              disabled={importExternalDocument.isPending}
            >
              {MOCK_EXTERNAL_SOURCES.map((source) => (
                <MenuItem key={source.id} value={source.id}>
                  {source.label}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <FormControl fullWidth size="small">
            <InputLabel id="external-document-label">{de.externalDocuments.documentLabel}</InputLabel>
            <Select
              labelId="external-document-label"
              label={de.externalDocuments.documentLabel}
              value={documentId}
              onChange={(event) => setDocumentId(event.target.value)}
              disabled={importExternalDocument.isPending}
            >
              {selectedSource.documents.map((document) => (
                <MenuItem key={document.id} value={document.id}>
                  {document.filename}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <Stack spacing={0.25}>
            <Typography variant="body2" sx={{ fontWeight: 600 }}>
              {selectedDocument.filename}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {selectedDocument.expectedOutcome}
            </Typography>
          </Stack>

          {importExternalDocument.isPending && <LinearProgress />}
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleDialogClose} disabled={importExternalDocument.isPending}>
          {de.common.actions.close}
        </Button>
        <Button
          variant="contained"
          startIcon={<CloudDownloadOutlined />}
          onClick={() => importExternalDocument.mutate(selectedDocument.id)}
          disabled={importExternalDocument.isPending}
        >
          {de.externalDocuments.importAction}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
