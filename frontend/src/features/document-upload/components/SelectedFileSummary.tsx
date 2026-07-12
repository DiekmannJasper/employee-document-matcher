import DescriptionOutlined from "@mui/icons-material/DescriptionOutlined";
import { Button, Stack, Typography } from "@mui/material";
import { formatFileSize } from "../../../shared/format/formatFileSize";
import { de } from "../../../shared/i18n/de";

interface SelectedFileSummaryProps {
  readonly file: File;
  readonly onChangeFile: () => void;
  readonly disabled?: boolean;
}

export function SelectedFileSummary({ file, onChangeFile, disabled = false }: SelectedFileSummaryProps) {
  return (
    <Stack
      direction="row"
      spacing={2}
      sx={{ p: 2, border: 1, borderColor: "divider", borderRadius: 1, alignItems: "center" }}
    >
      <DescriptionOutlined color="action" />
      <Stack sx={{ flexGrow: 1, minWidth: 0 }}>
        <Typography noWrap title={file.name}>
          {file.name}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {formatFileSize(file.size)}
        </Typography>
      </Stack>
      <Button size="small" onClick={onChangeFile} disabled={disabled}>
        {de.upload.changeFile}
      </Button>
    </Stack>
  );
}
