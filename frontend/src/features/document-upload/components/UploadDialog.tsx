import { Dialog, DialogContent, DialogTitle, IconButton, Typography } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";

interface UploadDialogProps {
  readonly open: boolean;
  readonly onClose: () => void;
}

export function UploadDialog({ open, onClose }: UploadDialogProps) {
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle sx={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
        PDF hochladen
        <IconButton aria-label="Schließen" onClick={onClose} size="small">
          <CloseIcon fontSize="small" />
        </IconButton>
      </DialogTitle>
      <DialogContent>
        <Typography color="text.secondary">Der Upload-Dialog wird in Kürze ergänzt.</Typography>
      </DialogContent>
    </Dialog>
  );
}
