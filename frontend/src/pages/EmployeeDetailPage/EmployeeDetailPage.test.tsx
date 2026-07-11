import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { Route, Routes } from "react-router-dom";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { AppProviders } from "../../app/providers/AppProviders";
import * as useDocumentCategoriesModule from "../../features/document-categories/hooks/useDocumentCategories";
import * as useEmployeeDocumentsModule from "../../features/employee-documents/hooks/useEmployeeDocuments";
import * as useEmployeeModule from "../../features/employees/hooks/useEmployee";
import { EmployeeDetailPage } from "./EmployeeDetailPage";

vi.mock("../../features/employees/hooks/useEmployee");
vi.mock("../../features/document-categories/hooks/useDocumentCategories");
vi.mock("../../features/employee-documents/hooks/useEmployeeDocuments");

const useEmployee = vi.mocked(useEmployeeModule.useEmployee);
const useDocumentCategories = vi.mocked(useDocumentCategoriesModule.useDocumentCategories);
const useEmployeeDocuments = vi.mocked(useEmployeeDocumentsModule.useEmployeeDocuments);

const EMPLOYEE_ID = "10000000-0000-0000-0000-000000000001";

const EMPLOYEE = {
  id: EMPLOYEE_ID,
  personnelNumber: "EMP-1001",
  firstName: "Anna",
  lastName: "Müller",
  department: "Produktentwicklung",
};

const CATEGORIES = [
  { id: "20000000-0000-0000-0000-000000000001", code: "CONTRACT", displayName: "Verträge", origin: "STANDARD" as const },
];

function renderPage() {
  return render(
    <AppProviders>
      <Routes>
        <Route path="/employees/:employeeId" element={<EmployeeDetailPage />} />
      </Routes>
    </AppProviders>,
  );
}

describe("EmployeeDetailPage", () => {
  beforeEach(() => {
    window.history.pushState({}, "", `/employees/${EMPLOYEE_ID}`);
  });

  afterEach(() => {
    vi.resetAllMocks();
    window.history.pushState({}, "", "/");
  });

  it("shows a loading state while data is pending", () => {
    useEmployee.mockReturnValue({ isPending: true, isError: false, data: undefined, refetch: vi.fn() } as never);
    useDocumentCategories.mockReturnValue({ isPending: true, isError: false, data: undefined, refetch: vi.fn() } as never);
    useEmployeeDocuments.mockReturnValue({ isPending: true, isError: false, data: undefined, refetch: vi.fn() } as never);

    renderPage();

    expect(screen.getByRole("status")).toBeInTheDocument();
  });

  it("shows an error state when a request fails", () => {
    useEmployee.mockReturnValue({ isPending: false, isError: true, data: undefined, refetch: vi.fn() } as never);
    useDocumentCategories.mockReturnValue({ isPending: false, isError: false, data: [], refetch: vi.fn() } as never);
    useEmployeeDocuments.mockReturnValue({ isPending: false, isError: false, data: [], refetch: vi.fn() } as never);

    renderPage();

    expect(screen.getByRole("alert")).toBeInTheDocument();
  });

  it("shows the employee master data and an empty document state", () => {
    useEmployee.mockReturnValue({ isPending: false, isError: false, data: EMPLOYEE, refetch: vi.fn() } as never);
    useDocumentCategories.mockReturnValue({ isPending: false, isError: false, data: CATEGORIES, refetch: vi.fn() } as never);
    useEmployeeDocuments.mockReturnValue({ isPending: false, isError: false, data: [], refetch: vi.fn() } as never);

    renderPage();

    expect(screen.getByText("Anna Müller")).toBeInTheDocument();
    expect(screen.getByRole("tab", { name: "Alle (0)" })).toBeInTheDocument();
    expect(screen.getByRole("tab", { name: "Verträge (0)" })).toBeInTheDocument();
    expect(screen.getByText("In dieser Kategorie liegen noch keine Dokumente vor.")).toBeInTheDocument();
  });

  it("groups documents into folders and filters the list on tab selection", async () => {
    const user = userEvent.setup();
    useEmployee.mockReturnValue({ isPending: false, isError: false, data: EMPLOYEE, refetch: vi.fn() } as never);
    useDocumentCategories.mockReturnValue({ isPending: false, isError: false, data: CATEGORIES, refetch: vi.fn() } as never);
    useEmployeeDocuments.mockReturnValue({
      isPending: false,
      isError: false,
      data: [
        {
          id: "30000000-0000-0000-0000-000000000001",
          originalFilename: "vertrag.pdf",
          categoryId: CATEGORIES[0].id,
          uploadedAt: "2026-01-15T10:00:00Z",
        },
        {
          id: "30000000-0000-0000-0000-000000000002",
          originalFilename: "sonstiges.pdf",
          categoryId: null,
          uploadedAt: "2026-02-20T10:00:00Z",
        },
      ],
      refetch: vi.fn(),
    } as never);

    renderPage();

    expect(screen.getByText("vertrag.pdf")).toBeInTheDocument();
    expect(screen.getByText("sonstiges.pdf")).toBeInTheDocument();

    await user.click(screen.getByRole("tab", { name: "Verträge (1)" }));

    expect(screen.getByText("vertrag.pdf")).toBeInTheDocument();
    expect(screen.queryByText("sonstiges.pdf")).not.toBeInTheDocument();
  });
});
