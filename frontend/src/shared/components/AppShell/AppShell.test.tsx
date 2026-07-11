import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it } from "vitest";
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
  it("renders the app title and page content", () => {
    renderAppShell();

    expect(screen.getByRole("heading", { name: "Employee Document Matcher" })).toBeInTheDocument();
    expect(screen.getByText("page content")).toBeInTheDocument();
  });

  it("toggles navigation from the topbar arrow button", async () => {
    const user = userEvent.setup();
    renderAppShell();

    await user.click(screen.getByRole("button", { name: "Navigation vergrößern" }));

    // The open mobile drawer is a modal; MUI marks the rest of the app aria-hidden
    // while it's open, so the toggle button itself is intentionally not queried here.
    expect(screen.getByRole("link", { name: "Mitarbeiter" })).toBeInTheDocument();
  });

  it("opens the upload dialog from the global upload button", async () => {
    const user = userEvent.setup();
    renderAppShell();

    await user.click(screen.getByRole("button", { name: "PDF hochladen" }));

    expect(screen.getByRole("dialog", { name: "PDF hochladen" })).toBeInTheDocument();
  });

  it("always shows a fixed footer with a back button", () => {
    renderAppShell();

    expect(screen.getByRole("button", { name: "Zurück" })).toBeInTheDocument();
  });
});
