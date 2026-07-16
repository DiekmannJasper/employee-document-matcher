import { useContext } from "react";
import { de } from "../i18n/de";
import { ToastContext } from "./ToastContext";

export function useToast() {
  const context = useContext(ToastContext);

  if (!context) {
    throw new Error(de.app.errors.missingToastProvider);
  }

  return context;
}
