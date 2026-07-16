import { Alert, Snackbar } from "@mui/material";
import { useCallback, useMemo, useState, type PropsWithChildren } from "react";
import { ToastContext, type ToastMessage } from "./ToastContext";

export function ToastProvider({ children }: PropsWithChildren) {
  const [toast, setToast] = useState<ToastMessage | null>(null);

  const showToast = useCallback((nextToast: ToastMessage) => {
    setToast(nextToast);
  }, []);

  const value = useMemo(() => ({ showToast }), [showToast]);

  function handleClose(_event?: unknown, reason?: string) {
    if (reason === "clickaway") {
      return;
    }
    setToast(null);
  }

  return (
    <ToastContext.Provider value={value}>
      {children}
      <Snackbar
        open={toast !== null}
        autoHideDuration={5000}
        onClose={handleClose}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        {toast ? (
          <Alert onClose={handleClose} severity={toast.severity ?? "info"} variant="filled" sx={{ width: "100%" }}>
            {toast.message}
          </Alert>
        ) : undefined}
      </Snackbar>
    </ToastContext.Provider>
  );
}
