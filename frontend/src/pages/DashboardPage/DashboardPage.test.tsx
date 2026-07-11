import { render, screen } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { AppProviders } from "../../app/providers/AppProviders";
import * as useEmployeesModule from "../../features/employees/hooks/useEmployees";
import { DashboardPage } from "./DashboardPage";

vi.mock("../../features/employees/hooks/useEmployees");

const useEmployees = vi.mocked(useEmployeesModule.useEmployees);

function renderDashboard() {
  return render(
    <AppProviders>
      <DashboardPage />
    </AppProviders>,
  );
}

describe("DashboardPage", () => {
  afterEach(() => {
    vi.resetAllMocks();
  });

  it("shows a loading state while employees are pending", () => {
    useEmployees.mockReturnValue({ isPending: true, isError: false, data: undefined } as never);

    renderDashboard();

    expect(screen.getByRole("status")).toBeInTheDocument();
  });

  it("shows an error state when loading fails", () => {
    useEmployees.mockReturnValue({ isPending: false, isError: true, data: undefined } as never);

    renderDashboard();

    expect(screen.getByRole("alert")).toBeInTheDocument();
  });

  it("shows an empty state when there are no employees", () => {
    useEmployees.mockReturnValue({ isPending: false, isError: false, data: [] } as never);

    renderDashboard();

    expect(screen.getByText("Es sind noch keine Mitarbeiter angelegt.")).toBeInTheDocument();
  });

  it("renders the employee grid once data has loaded", () => {
    useEmployees.mockReturnValue({
      isPending: false,
      isError: false,
      data: [
        {
          id: "10000000-0000-0000-0000-000000000001",
          personnelNumber: "EMP-1001",
          firstName: "Anna",
          lastName: "Müller",
          department: "Produktentwicklung",
        },
      ],
    } as never);

    renderDashboard();

    expect(screen.getByText("Anna Müller")).toBeInTheDocument();
  });
});
