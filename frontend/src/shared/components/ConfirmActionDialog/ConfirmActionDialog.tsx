import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";
import { de } from "../../i18n/de";

interface ConfirmActionDialogProps {
  readonly open: boolean;
  readonly title: string;
  readonly message: string;
  readonly confirmLabel?: string;
  readonly confirmDisabled?: boolean;
  readonly onCancel: () => void;
  readonly onConfirm: () => void;
}

export function ConfirmActionDialog({
  open,
  title,
  message,
  confirmLabel = de.common.actions.confirm,
  confirmDisabled = false,
  onCancel,
  onConfirm,
}: ConfirmActionDialogProps) {
  return (
    <Dialog open={open} onClose={onCancel} fullWidth maxWidth="xs">
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        <DialogContentText>{message}</DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={onCancel}>{de.common.actions.cancel}</Button>
        <Button variant="contained" onClick={onConfirm} disabled={confirmDisabled}>
          {confirmLabel}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
