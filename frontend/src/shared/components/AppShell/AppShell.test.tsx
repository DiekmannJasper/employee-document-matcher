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

  it("opens navigation from the menu button and links to the dashboard", async () => {
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
});
