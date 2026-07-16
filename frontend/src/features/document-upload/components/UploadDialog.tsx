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
import { de } from "../../../shared/i18n/de";
import { useUploadDocument } from "../hooks/useUploadDocument";
import { isSupportedDocumentFile } from "../utils/isSupportedDocumentFile";
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

  const isValidFile = selectedFile ? isSupportedDocumentFile(selectedFile) : true;

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
    if (!selectedFile || !isSupportedDocumentFile(selectedFile)) {
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
        {de.upload.title}
        <IconButton aria-label={de.common.actions.close} onClick={handleDialogClose} size="small">
          <CloseIcon fontSize="small" />
        </IconButton>
      </DialogTitle>
      <DialogContent>
        <Stack spacing={2}>
          <Stack spacing={1}>
            <Typography variant="subtitle2">{de.upload.title}</Typography>
            <Typography variant="body2" color="text.secondary">
              {de.upload.manualHint}
            </Typography>
          </Stack>
          {!selectedFile && <FileDropZone onFileSelected={handleFileSelected} />}

          {selectedFile && (
            <SelectedFileSummary
              file={selectedFile}
              onChangeFile={handleChangeFile}
              disabled={uploadMutation.isPending}
            />
          )}

          {selectedFile && !isValidFile && <Alert severity="error">{de.upload.invalidFile}</Alert>}

          {uploadMutation.isPending && (
            <Stack spacing={1}>
              <LinearProgress />
              <Typography variant="body2" color="text.secondary">
                {de.upload.inProgress}
              </Typography>
            </Stack>
          )}

          {uploadMutation.isError && !isAbortError(uploadMutation.error) && (
            <Alert severity="error">
              {uploadMutation.error instanceof ApiError
                ? uploadMutation.error.message
                : de.upload.error}
            </Alert>
          )}

          {uploadMutation.isSuccess && (
            <Alert severity="success">{de.upload.success(uploadMutation.data.originalFilename)}</Alert>
          )}
        </Stack>
      </DialogContent>
      <DialogActions>
        {uploadMutation.isPending && (
          <Button onClick={handleCancelUpload} color="inherit">
            {de.common.actions.cancel}
          </Button>
        )}
        {!uploadMutation.isPending && !uploadMutation.isSuccess && (
          <Button onClick={handleUpload} variant="contained" disabled={!selectedFile || !isValidFile}>
            {de.upload.action}
          </Button>
        )}
        {uploadMutation.isSuccess && (
          <Button onClick={handleDialogClose} variant="contained">
            {de.common.actions.done}
          </Button>
        )}
      </DialogActions>
    </Dialog>
  );
}
