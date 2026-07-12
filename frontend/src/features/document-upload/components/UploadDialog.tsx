import CloseIcon from "@mui/icons-material/Close";
import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  LinearProgress,
  Stack,
  Typography,
} from "@mui/material";
import { useRef, useState } from "react";
import { ApiError } from "../../../shared/api/httpClient";
import { useUploadDocument } from "../hooks/useUploadDocument";
import { isPdfFile } from "../utils/isPdfFile";
import { FileDropZone } from "./FileDropZone";
import { SelectedFileSummary } from "./SelectedFileSummary";

interface UploadDialogProps {
  readonly open: boolean;
  readonly onClose: () => void;
}

function isAbortError(error: unknown): boolean {
  return error instanceof DOMException && error.name === "AbortError";
}

// Holds per-session state (selected file, mutation result). The AppShell remounts
// it with a fresh key on every open so a reopened dialog always starts empty.
export function UploadDialog({ open, onClose }: UploadDialogProps) {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const abortControllerRef = useRef<AbortController | null>(null);
  const uploadMutation = useUploadDocument();

  const isValidPdf = selectedFile ? isPdfFile(selectedFile) : true;

  function handleFileSelected(file: File) {
    uploadMutation.reset();
    setSelectedFile(file);
  }

  function handleChangeFile() {
    abortControllerRef.current?.abort();
    uploadMutation.reset();
    setSelectedFile(null);
  }

  function handleUpload() {
    if (!selectedFile || !isPdfFile(selectedFile)) {
      return;
    }

    const controller = new AbortController();
    abortControllerRef.current = controller;
    uploadMutation.mutate(
      { file: selectedFile, signal: controller.signal },
      {
        onError: (error) => {
          if (isAbortError(error)) {
            uploadMutation.reset();
          }
        },
      },
    );
  }

  function handleCancelUpload() {
    abortControllerRef.current?.abort();
  }

  function handleDialogClose() {
    if (uploadMutation.isPending) {
      return;
    }
    onClose();
  }

  return (
    <Dialog open={open} onClose={handleDialogClose} fullWidth maxWidth="sm">
      <DialogTitle sx={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
        PDF hochladen
        <IconButton aria-label="Schließen" onClick={handleDialogClose} size="small">
          <CloseIcon fontSize="small" />
        </IconButton>
      </DialogTitle>
      <DialogContent>
        <Stack spacing={2}>
          {!selectedFile && <FileDropZone onFileSelected={handleFileSelected} />}

          {selectedFile && (
            <SelectedFileSummary
              file={selectedFile}
              onChangeFile={handleChangeFile}
              disabled={uploadMutation.isPending}
            />
          )}

          {selectedFile && !isValidPdf && <Alert severity="error">Nur PDF-Dateien werden unterstützt.</Alert>}

          {uploadMutation.isPending && (
            <Stack spacing={1}>
              <LinearProgress />
              <Typography variant="body2" color="text.secondary">
                Datei wird hochgeladen…
              </Typography>
            </Stack>
          )}

          {uploadMutation.isError && !isAbortError(uploadMutation.error) && (
            <Alert severity="error">
              {uploadMutation.error instanceof ApiError
                ? uploadMutation.error.message
                : "Die Datei konnte nicht hochgeladen werden."}
            </Alert>
          )}

          {uploadMutation.isSuccess && (
            <Alert severity="success">„{uploadMutation.data.originalFilename}“ wurde erfolgreich hochgeladen.</Alert>
          )}
        </Stack>
      </DialogContent>
      <DialogActions>
        {uploadMutation.isPending && (
          <Button onClick={handleCancelUpload} color="inherit">
            Abbrechen
          </Button>
        )}
        {!uploadMutation.isPending && !uploadMutation.isSuccess && (
          <Button onClick={handleUpload} variant="contained" disabled={!selectedFile || !isValidPdf}>
            Hochladen
          </Button>
        )}
        {uploadMutation.isSuccess && (
          <Button onClick={handleDialogClose} variant="contained">
            Fertig
          </Button>
        )}
      </DialogActions>
    </Dialog>
  );
}
