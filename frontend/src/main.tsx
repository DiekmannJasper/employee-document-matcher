import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { App } from "./App";
import { AppProviders } from "./app/providers/AppProviders";
import "./styles/index.css";
import { de } from "./shared/i18n/de";

const rootElement = document.getElementById("root");

if (!rootElement) {
  throw new Error(de.app.errors.missingRootElement);
}

createRoot(rootElement).render(
  <StrictMode>
    <AppProviders>
      <App />
    </AppProviders>
  </StrictMode>,
);
