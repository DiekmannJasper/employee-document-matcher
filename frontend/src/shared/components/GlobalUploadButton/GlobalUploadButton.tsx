import UploadFile from "@mui/icons-material/UploadFile";
import { Button, IconButton, useMediaQuery, type Theme } from "@mui/material";
import { de } from "../../i18n/de";

interface GlobalUploadButtonProps {
  readonly onClick: () => void;
}

export function GlobalUploadButton({ onClick }: GlobalUploadButtonProps) {
  const isCompact = useMediaQuery<Theme>((theme) => theme.breakpoints.down("sm"));

  if (isCompact) {
    return (
      <IconButton color="inherit" aria-label={de.upload.title} onClick={onClick}>
        <UploadFile />
      </IconButton>
    );
  }

  return (
    <Button color="inherit" variant="outlined" startIcon={<UploadFile />} onClick={onClick}>
      {de.upload.title}
    </Button>
  );
}
