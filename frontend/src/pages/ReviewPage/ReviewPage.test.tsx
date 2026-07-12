import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, describe, expect, it, vi } from "vitest";
import { AppProviders } from "../../app/providers/AppProviders";
import * as useDocumentCategoriesModule from "../../features/document-categories/hooks/useDocumentCategories";
import * as useConfirmMatchModule from "../../features/document-review/hooks/useConfirmMatch";
import * as usePendingReviewsModule from "../../features/document-review/hooks/usePendingReviews";
import * as useEmployeesModule from "../../features/employees/hooks/useEmployees";
import { ReviewPage } from "./ReviewPage";

vi.mock("../../features/document-review/hooks/usePendingReviews");
vi.mock("../../features/document-review/hooks/useConfirmMatch");
vi.mock("../../features/employees/hooks/useEmployees");
vi.mock("../../features/document-categories/hooks/useDocumentCategories");

const usePendingReviews = vi.mocked(usePendingReviewsModule.usePendingReviews);
const useConfirmMatch = vi.mocked(useConfirmMatchModule.useConfirmMatch);
const useEmployees = vi.mocked(useEmployeesModule.useEmployees);
const useDocumentCategories = vi.mocked(useDocumentCategoriesModule.useDocumentCategories);

const EMPLOYEE = {
  id: "10000000-0000-0000-0000-000000000001",
  personnelNumber: "EMP-1001",
  firstName: "Anna",
  lastName: "Müller",
  department: "Produktentwicklung",
};

const CATEGORY = {
  id: "20000000-0000-0000-0000-000000000001",
  code: "CONTRACT",
  displayName: "Verträge",
  origin: "STANDARD" as const,
};

function renderPage() {
  return render(
    <AppProviders>
      <ReviewPage />
    </AppProviders>,
  );
}

function idleConfirmMutation(overrides: Record<string, unknown> = {}) {
  return {
    mutate: vi.fn(),
    isPending: false,
    isError: false,
    isSuccess: false,
    error: null,
    ...overrides,
  };
}

describe("ReviewPage", () => {
  afterEach(() => {
    vi.resetAllMocks();
  });

  it("shows a loading state while pending reviews are loading", () => {
    usePendingReviews.mockReturnValue({ isPending: true, isError: false, data: undefined, refetch: vi.fn() } as never);
    useEmployees.mockReturnValue({ isPending: false, isError: false, data: [EMPLOYEE], refetch: vi.fn() } as never);
    useDocumentCategories.mockReturnValue({ isPending: false, isError: false, data: [CATEGORY], refetch: vi.fn() } as never);
    useConfirmMatch.mockReturnValue(idleConfirmMutation() as never);

    renderPage();

    expect(screen.getByRole("status")).toBeInTheDocument();
  });

  it("shows an empty state when there are no pending reviews", () => {
    usePendingReviews.mockReturnValue({ isPending: false, isError: false, data: [], refetch: vi.fn() } as never);
    useEmployees.mockReturnValue({ isPending: false, isError: false, data: [EMPLOYEE], refetch: vi.fn() } as never);
    useDocumentCategories.mockReturnValue({ isPending: false, isError: false, data: [CATEGORY], refetch: vi.fn() } as never);
    useConfirmMatch.mockReturnValue(idleConfirmMutation() as never);

    renderPage();

    expect(screen.getByText("Es liegen aktuell keine offenen Prüffälle vor.")).toBeInTheDocument();
  });

  it("shows a suggested match and confirms it with the suggested category", async () => {
    const user = userEvent.setup();
    const mutate = vi.fn();
    usePendingReviews.mockReturnValue({
      isPending: false,
      isError: false,
      data: [
        {
          documentId: "30000000-0000-0000-0000-000000000001",
          originalFilename: "vertrag.pdf",
          matchStatus: "MATCHED",
          suggestedEmployeeId: EMPLOYEE.id,
          evidence: "Name im Dokument gefunden: 'Anna Müller'",
          systemScore: "HIGH",
          suggestedCategoryId: CATEGORY.id,
          suggestedCategoryName: null,
          categoryEvidence: "Schlüsselwort erkannt: 'vertrag'",
          llmConfidence: "HIGH",
          uploadedAt: "2026-01-15T10:00:00Z",
        },
      ],
      refetch: vi.fn(),
    } as never);
    useEmployees.mockReturnValue({ isPending: false, isError: false, data: [EMPLOYEE], refetch: vi.fn() } as never);
    useDocumentCategories.mockReturnValue({ isPending: false, isError: false, data: [CATEGORY], refetch: vi.fn() } as never);
    useConfirmMatch.mockReturnValue(idleConfirmMutation({ mutate }) as never);

    renderPage();

    expect(screen.getByText("vertrag.pdf")).toBeInTheDocument();
    expect(screen.getByText("Eindeutiger Treffer")).toBeInTheDocument();
    expect(screen.getByText("System-Score: Hoch")).toBeInTheDocument();
    expect(screen.getByText("KI-Konfidenz: Hoch")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Bestätigen" }));

    expect(mutate).toHaveBeenCalledWith({
      documentId: "30000000-0000-0000-0000-000000000001",
      employeeId: EMPLOYEE.id,
      categoryId: CATEGORY.id,
      newCategoryName: null,
    });
  });

  it("confirms a suggested new category as free text", async () => {
    const user = userEvent.setup();
    const mutate = vi.fn();
    usePendingReviews.mockReturnValue({
      isPending: false,
      isError: false,
      data: [
        {
          documentId: "30000000-0000-0000-0000-000000000003",
          originalFilename: "kuendigung.pdf",
          matchStatus: "NO_MATCH",
          suggestedEmployeeId: null,
          evidence: "Schlüsselwort erkannt: 'kündigung'",
          systemScore: "NONE",
          suggestedCategoryId: null,
          suggestedCategoryName: "Kündigungen",
          categoryEvidence: "Schlüsselwort erkannt: 'kündigung'",
          llmConfidence: "MEDIUM",
          uploadedAt: "2026-01-15T10:00:00Z",
        },
      ],
      refetch: vi.fn(),
    } as never);
    useEmployees.mockReturnValue({ isPending: false, isError: false, data: [EMPLOYEE], refetch: vi.fn() } as never);
    useDocumentCategories.mockReturnValue({ isPending: false, isError: false, data: [CATEGORY], refetch: vi.fn() } as never);
    useConfirmMatch.mockReturnValue(idleConfirmMutation({ mutate }) as never);

    renderPage();

    expect(screen.getByText('Vorschlag für neue Kategorie: "Kündigungen"')).toBeInTheDocument();
    expect(screen.getByDisplayValue("Kündigungen")).toBeInTheDocument();
    expect(screen.getByText("Kategorie-Begründung: Schlüsselwort erkannt: 'kündigung'")).toBeInTheDocument();
    expect(screen.getByText("System-Score: Kein Signal")).toBeInTheDocument();
    expect(screen.getByText("KI-Konfidenz: Mittel")).toBeInTheDocument();

    await user.click(screen.getByRole("combobox", { name: "Mitarbeiter auswählen" }));
    await user.click(await screen.findByRole("option", { name: /Anna Müller/ }));
    await user.click(screen.getByRole("button", { name: "Bestätigen" }));

    expect(mutate).toHaveBeenCalledWith({
      documentId: "30000000-0000-0000-0000-000000000003",
      employeeId: EMPLOYEE.id,
      categoryId: null,
      newCategoryName: "Kündigungen",
    });
  });

  it("disables confirmation until an employee is selected for an unmatched document", () => {
    usePendingReviews.mockReturnValue({
      isPending: false,
      isError: false,
      data: [
        {
          documentId: "30000000-0000-0000-0000-000000000002",
          originalFilename: "sonstiges.pdf",
          matchStatus: "NO_MATCH",
          suggestedEmployeeId: null,
          evidence: "Kein Mitarbeitername im Dokument gefunden.",
          systemScore: "NONE",
          suggestedCategoryId: null,
          suggestedCategoryName: null,
          categoryEvidence: null,
          llmConfidence: "NONE",
          uploadedAt: "2026-01-15T10:00:00Z",
        },
      ],
      refetch: vi.fn(),
    } as never);
    useEmployees.mockReturnValue({ isPending: false, isError: false, data: [EMPLOYEE], refetch: vi.fn() } as never);
    useDocumentCategories.mockReturnValue({ isPending: false, isError: false, data: [CATEGORY], refetch: vi.fn() } as never);
    useConfirmMatch.mockReturnValue(idleConfirmMutation() as never);

    renderPage();

    expect(screen.getByRole("button", { name: "Bestätigen" })).toBeDisabled();
  });
});
