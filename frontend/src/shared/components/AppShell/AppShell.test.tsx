import { render, screen, waitForElementToBeRemoved } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, describe, expect, it } from "vitest";
import { AppProviders } from "../../../app/providers/AppProviders";
import { AppShell } from "./AppShell";

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

    expect(screen.getByRole("heading", { name: "Employee Document Matcher" })).toBeInTheDocument();
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

    await user.click(screen.getByRole("button", { name: "PDF hochladen" }));

    expect(screen.getByRole("dialog", { name: "PDF hochladen" })).toBeInTheDocument();
  });

  it("starts a fresh upload session when the dialog is reopened", async () => {
    const user = userEvent.setup();
    renderAppShell();

    await user.click(screen.getByRole("button", { name: "PDF hochladen" }));
    const input = document.querySelector('input[type="file"]') as HTMLInputElement;
    await user.upload(input, new File(["%PDF-1.4"], "vertrag.pdf", { type: "application/pdf" }));
    expect(screen.getByText("vertrag.pdf")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Schließen" }));
    await waitForElementToBeRemoved(() => screen.queryByRole("dialog"));
    await user.click(screen.getByRole("button", { name: "PDF hochladen" }));

    expect(screen.getByRole("button", { name: "PDF-Datei auswählen oder hierher ziehen" })).toBeInTheDocument();
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
