import UploadFile from "@mui/icons-material/UploadFile";
import { Box, Typography } from "@mui/material";
import { useRef, useState, type DragEvent, type KeyboardEvent } from "react";
import { de } from "../../../shared/i18n/de";

interface FileDropZoneProps {
  readonly onFileSelected: (file: File) => void;
}

export function FileDropZone({ onFileSelected }: FileDropZoneProps) {
  const [isDragActive, setIsDragActive] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  function openFileBrowser() {
    inputRef.current?.click();
  }

  function handleKeyDown(event: KeyboardEvent<HTMLDivElement>) {
    if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      openFileBrowser();
    }
  }

  function handleDrop(event: DragEvent<HTMLDivElement>) {
    event.preventDefault();
    setIsDragActive(false);
    const file = event.dataTransfer.files[0];
    if (file) {
      onFileSelected(file);
    }
  }

  return (
    <Box
      role="button"
      tabIndex={0}
      aria-label={de.upload.chooseAriaLabel}
      onClick={openFileBrowser}
      onKeyDown={handleKeyDown}
      onDragOver={(event) => {
        event.preventDefault();
        setIsDragActive(true);
      }}
      onDragLeave={() => setIsDragActive(false)}
      onDrop={handleDrop}
      sx={{
        border: "2px dashed",
        borderColor: isDragActive ? "primary.main" : "divider",
        borderRadius: 1,
        p: 4,
        textAlign: "center",
        cursor: "pointer",
        backgroundColor: isDragActive ? "action.hover" : "transparent",
      }}
    >
      <UploadFile fontSize="large" color={isDragActive ? "primary" : "disabled"} />
      <Typography sx={{ mt: 1 }}>{de.upload.dropZoneHint}</Typography>
      <input
        ref={inputRef}
        type="file"
        accept="application/pdf"
        hidden
        onChange={(event) => {
          const file = event.target.files?.[0];
          if (file) {
            onFileSelected(file);
          }
          event.target.value = "";
        }}
      />
    </Box>
  );
}
