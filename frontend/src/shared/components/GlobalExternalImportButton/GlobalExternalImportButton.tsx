import CloudDownloadOutlined from "@mui/icons-material/CloudDownloadOutlined";
import { Button, IconButton, useMediaQuery, type Theme } from "@mui/material";
import { de } from "../../i18n/de";

interface GlobalExternalImportButtonProps {
  readonly onClick: () => void;
}

export function GlobalExternalImportButton({ onClick }: GlobalExternalImportButtonProps) {
  const isCompact = useMediaQuery<Theme>((theme) => theme.breakpoints.down("sm"));

  if (isCompact) {
    return (
      <IconButton color="inherit" aria-label={de.externalDocuments.action} onClick={onClick}>
        <CloudDownloadOutlined />
      </IconButton>
    );
  }

  return (
    <Button color="inherit" variant="outlined" startIcon={<CloudDownloadOutlined />} onClick={onClick}>
      {de.externalDocuments.action}
    </Button>
  );
}
