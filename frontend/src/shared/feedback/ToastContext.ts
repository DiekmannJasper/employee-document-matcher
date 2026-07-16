import { createContext } from "react";

export type ToastSeverity = "success" | "error" | "info" | "warning";

export interface ToastMessage {
  readonly message: string;
  readonly severity?: ToastSeverity;
}

interface ToastContextValue {
  readonly showToast: (toast: ToastMessage) => void;
}

export const ToastContext = createContext<ToastContextValue | null>(null);
