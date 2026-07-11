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

function mockDesktopViewport() {
  window.matchMedia = (query: string) => ({
    matches: query.includes("min-width"),
    media: query,
    onchange: null,
    addListener: () => {},
    removeListener: () => {},
    addEventListener: () => {},
    removeEventListener: () => {},
    dispatchEvent: () => false,
  });
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

  describe("on desktop", () => {
    const originalMatchMedia = window.matchMedia;

    beforeEach(mockDesktopViewport);

    afterEach(() => {
      window.matchMedia = originalMatchMedia;
    });

    it("collapses the drawer to a mini rail via the in-drawer toggle", async () => {
      const user = userEvent.setup();
      renderAppShell();

      await user.click(screen.getByRole("button", { name: "Navigation verkleinern" }));

      expect(screen.getByRole("button", { name: "Navigation vergrößern" })).toBeInTheDocument();
    });

    it("shows a back button at the top of the navigation, disabled on the dashboard", () => {
      renderAppShell();

      expect(screen.getByRole("button", { name: "Zurück" })).toBeDisabled();
    });

    it("enables the back button away from the dashboard", () => {
      window.history.pushState({}, "", "/employees/10000000-0000-0000-0000-000000000001");
      renderAppShell();

      expect(screen.getByRole("button", { name: "Zurück" })).toBeEnabled();
    });
  });
});
