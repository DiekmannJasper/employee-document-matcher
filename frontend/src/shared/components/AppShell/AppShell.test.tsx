import { render, screen, waitForElementToBeRemoved } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { AppProviders } from "../../../app/providers/AppProviders";
import { AppShell } from "./AppShell";

vi.mock("../../../features/document-upload/components/ExternalImportDialog", () => ({
  ExternalImportDialog: ({ open }: { open: boolean }) => (open ? <div role="dialog" aria-label="Externe Dokumentquelle" /> : null),
}));

function renderAppShell() {
  return render(
    <AppProviders>
      <AppShell>
        <div>page content</div>
      </AppShell>
    </AppProviders>,
  );
}

describe("AppShell", () => {
  afterEach(() => {
    window.history.pushState({}, "", "/");
  });

  it("renders the app title and page content", () => {
    renderAppShell();

    expect(screen.getByRole("heading", { name: "Personal Dokumenten Verwaltung" })).toBeInTheDocument();
    expect(screen.getByText("page content")).toBeInTheDocument();
  });

  it("opens mobile navigation from the topbar menu button", async () => {
    const user = userEvent.setup();
    renderAppShell();

    await user.click(screen.getByRole("button", { name: "Navigation öffnen" }));

    expect(screen.getByRole("link", { name: "Mitarbeiter" })).toBeInTheDocument();
  });

  it("opens the upload dialog from the global upload button", async () => {
    const user = userEvent.setup();
    renderAppShell();

    await user.click(screen.getByRole("button", { name: "Manuell hochladen" }));

    expect(screen.getByRole("dialog", { name: "Dokument hochladen" })).toBeInTheDocument();
  });

  it("opens the external import dialog from the topbar button", async () => {
    const user = userEvent.setup();
    renderAppShell();

    await user.click(screen.getByRole("button", { name: "Extern importieren" }));

    expect(screen.getByRole("dialog", { name: "Externe Dokumentquelle" })).toBeInTheDocument();
  });

  it("shows the global search only on the employee list", () => {
    renderAppShell();

    expect(screen.getByPlaceholderText("Mitarbeiter suchen…")).toBeInTheDocument();
  });

  it("hides the global search away from the employee list", () => {
    window.history.pushState({}, "", "/employees/10000000-0000-0000-0000-000000000001");
    renderAppShell();

    expect(screen.queryByPlaceholderText("Mitarbeiter suchen…")).not.toBeInTheDocument();
  });

  it("starts a fresh upload session when the dialog is reopened", async () => {
    const user = userEvent.setup();
    renderAppShell();

    await user.click(screen.getByRole("button", { name: "Manuell hochladen" }));
    const input = document.querySelector('input[type="file"]') as HTMLInputElement;
    await user.upload(input, new File(["%PDF-1.4"], "vertrag.pdf", { type: "application/pdf" }));
    expect(screen.getByText("vertrag.pdf")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Schließen" }));
    await waitForElementToBeRemoved(() => screen.queryByRole("dialog"));
    await user.click(screen.getByRole("button", { name: "Manuell hochladen" }));

    expect(screen.getByRole("button", { name: "Datei auswählen oder hierher ziehen" })).toBeInTheDocument();
    expect(screen.queryByText("vertrag.pdf")).not.toBeInTheDocument();
  });

  it("shows a footer with the back button disabled on the dashboard", () => {
    renderAppShell();

    expect(screen.getByRole("button", { name: "Zurück" })).toBeDisabled();
  });

  it("enables the back button away from the dashboard", () => {
    window.history.pushState({}, "", "/employees/10000000-0000-0000-0000-000000000001");
    renderAppShell();

    expect(screen.getByRole("button", { name: "Zurück" })).toBeEnabled();
  });
});
