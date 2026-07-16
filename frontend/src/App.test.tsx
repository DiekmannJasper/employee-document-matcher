import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { App } from "./App";
import { AppProviders } from "./app/providers/AppProviders";

describe("App", () => {
  it("renders the application heading", () => {
    render(
      <AppProviders>
        <App />
      </AppProviders>,
    );

    expect(
      screen.getByRole("heading", { name: "Personal Dokumenten Verwaltung" }),
    ).toBeInTheDocument();
  });
});
