import { render, screen } from "@testing-library/react";
import { useEffect } from "react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { AppProviders } from "../../app/providers/AppProviders";
import * as useEmployeesModule from "../../features/employees/hooks/useEmployees";
import { SearchProvider } from "../../shared/search/SearchProvider";
import { useGlobalSearch } from "../../shared/search/useGlobalSearch";
import { DashboardPage } from "./DashboardPage";

vi.mock("../../features/employees/hooks/useEmployees");

const useEmployees = vi.mocked(useEmployeesModule.useEmployees);

function SearchQuerySetter({ value }: { readonly value: string }) {
  const { setQuery } = useGlobalSearch();
  useEffect(() => setQuery(value), [value, setQuery]);
  return null;
}

function renderDashboard(searchQuery = "") {
  return render(
    <AppProviders>
      <SearchProvider>
        <SearchQuerySetter value={searchQuery} />
        <DashboardPage />
      </SearchProvider>
    </AppProviders>,
  );
}

const TWO_EMPLOYEES = [
  {
    id: "10000000-0000-0000-0000-000000000001",
    personnelNumber: "EMP-1001",
    firstName: "Anna",
    lastName: "Müller",
    department: "Produktentwicklung",
  },
  {
    id: "10000000-0000-0000-0000-000000000002",
    personnelNumber: "EMP-1002",
    firstName: "David",
    lastName: "Schneider",
    department: "Vertrieb",
  },
];

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
    useEmployees.mockReturnValue({ isPending: false, isError: false, data: TWO_EMPLOYEES } as never);

    renderDashboard();

    expect(screen.getByText("Anna Müller")).toBeInTheDocument();
    expect(screen.getByText("David Schneider")).toBeInTheDocument();
  });

  it("filters the grid by the global search query", () => {
    useEmployees.mockReturnValue({ isPending: false, isError: false, data: TWO_EMPLOYEES } as never);

    renderDashboard("schneider");

    expect(screen.queryByText("Anna Müller")).not.toBeInTheDocument();
    expect(screen.getByText("David Schneider")).toBeInTheDocument();
  });

  it("shows a search-specific empty state when the query matches nothing", () => {
    useEmployees.mockReturnValue({ isPending: false, isError: false, data: TWO_EMPLOYEES } as never);

    renderDashboard("nonexistent-name");

    expect(screen.getByText("Keine Mitarbeiter gefunden.")).toBeInTheDocument();
  });
});
