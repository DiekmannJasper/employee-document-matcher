import DescriptionOutlined from "@mui/icons-material/DescriptionOutlined";
import { Box, Typography } from "@mui/material";
import { de } from "../../i18n/de";

const CONTENT_TYPE_LABELS: Record<string, string> = {
  "application/pdf": de.documents.formatLabels.pdf,
  "application/vnd.openxmlformats-officedocument.wordprocessingml.document": de.documents.formatLabels.docx,
  "application/xml": de.documents.formatLabels.xml,
  "text/xml": de.documents.formatLabels.xml,
};

function formatLabelFor(contentType: string): string {
  return CONTENT_TYPE_LABELS[contentType] ?? de.documents.formatLabels.unknown;
}

interface DocumentPreviewThumbnailProps {
  readonly fileUrl: string;
  readonly filename: string;
  readonly contentType: string;
}

// The browser can only render a PDF inline via an iframe. For other formats (Word, XML, ...) we
// show a format badge instead - opening the file (see the "Öffnen" action) still works for all
// formats since that's a plain download/inline-open through the browser, not a rendered preview.
export function DocumentPreviewThumbnail({ fileUrl, filename, contentType }: DocumentPreviewThumbnailProps) {
  const isPdf = contentType === "application/pdf";
  const previewUrl = `${fileUrl}#toolbar=0&navpanes=0&scrollbar=0&page=1&view=FitH`;

  return (
    <Box
      sx={{
        position: "relative",
        height: 220,
        borderBottom: 1,
        borderColor: "divider",
        bgcolor: "grey.100",
        overflow: "hidden",
      }}
    >
      {isPdf ? (
        <Box
          component="iframe"
          src={previewUrl}
          title={de.documents.previewFor(filename)}
          loading="lazy"
          sx={{
            width: "100%",
            height: "100%",
            border: 0,
            bgcolor: "background.paper",
          }}
        />
      ) : (
        <Box
          sx={{
            width: "100%",
            height: "100%",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            gap: 1,
            bgcolor: "background.paper",
          }}
        >
          <DescriptionOutlined sx={{ fontSize: 40 }} color="disabled" />
          <Typography variant="caption" color="text.secondary">
            {de.documents.previewUnavailable(formatLabelFor(contentType))}
          </Typography>
        </Box>
      )}
      <Box
        sx={{
          position: "absolute",
          right: 8,
          bottom: 8,
          display: "grid",
          placeItems: "center",
          width: 36,
          height: 36,
          borderRadius: 1,
          bgcolor: "background.paper",
          boxShadow: 1,
          color: "text.secondary",
        }}
      >
        <DescriptionOutlined fontSize="small" />
      </Box>
    </Box>
  );
}
