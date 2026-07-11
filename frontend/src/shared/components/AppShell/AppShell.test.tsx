import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it } from "vitest";
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
  beforeEach(() => {
    window.history.pushState({}, "", "/");
  });

  it("renders the app title and page content", () => {
    renderAppShell();

    expect(screen.getByRole("heading", { name: "Employee Document Matcher" })).toBeInTheDocument();
    expect(screen.getByText("page content")).toBeInTheDocument();
  });

  it("toggles navigation from the menu button", async () => {
    const user = userEvent.setup();
    renderAppShell();

    await user.click(screen.getByRole("button", { name: "Navigation umschalten" }));

    expect(screen.getByRole("link", { name: "Mitarbeiter" })).toBeInTheDocument();
  });

  it("opens the upload dialog from the global upload button", async () => {
    const user = userEvent.setup();
    renderAppShell();

    await user.click(screen.getByRole("button", { name: "PDF hochladen" }));

    expect(screen.getByRole("dialog", { name: "PDF hochladen" })).toBeInTheDocument();
  });

  it("does not show a footer or back button on the dashboard", () => {
    renderAppShell();

    expect(screen.queryByRole("button", { name: "Zurück" })).not.toBeInTheDocument();
  });

  describe("on a non-root route", () => {
    beforeEach(() => {
      window.history.pushState({}, "", "/employees/10000000-0000-0000-0000-000000000001");
    });

    it("shows a fixed footer with a back button", () => {
      renderAppShell();

      expect(screen.getByRole("button", { name: "Zurück" })).toBeInTheDocument();
    });
  });

  afterEach(() => {
    window.history.pushState({}, "", "/");
  });
});
