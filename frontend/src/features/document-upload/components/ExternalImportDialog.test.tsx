import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { AppProviders } from "../../../app/providers/AppProviders";
import * as useImportExternalDocumentModule from "../hooks/useImportExternalDocument";
import { ExternalImportDialog } from "./ExternalImportDialog";

vi.mock("../hooks/useImportExternalDocument");

const useImportExternalDocument = vi.mocked(useImportExternalDocumentModule.useImportExternalDocument);

function idleMutation(overrides: Record<string, unknown> = {}) {
  return {
    mutate: vi.fn(),
    isPending: false,
    ...overrides,
  };
}

function renderDialog() {
  return render(
    <AppProviders>
      <ExternalImportDialog open onClose={vi.fn()} />
    </AppProviders>,
  );
}

describe("ExternalImportDialog", () => {
  beforeEach(() => {
    useImportExternalDocument.mockReturnValue(idleMutation() as never);
  });

  it("selects a mocked external source and imports its selected document", async () => {
    const user = userEvent.setup();
    const mutate = vi.fn();
    useImportExternalDocument.mockReturnValue(idleMutation({ mutate }) as never);

    renderDialog();

    expect(screen.getAllByText("datev-gehaltsabrechnung-laura-hoffmann.pdf")).not.toHaveLength(0);
    await user.click(screen.getByRole("combobox", { name: "Dokument" }));
    expect(await screen.findByRole("option", { name: "datev-export-lohn-januar.zip" })).toBeInTheDocument();
    await user.keyboard("{Escape}");

    await user.click(screen.getByRole("combobox", { name: "System" }));
    expect(await screen.findByRole("option", { name: "Lexware Office" })).toBeInTheDocument();
    await user.click(await screen.findByRole("option", { name: "SAP SuccessFactors" }));
    expect(screen.getAllByText("successfactors-vertrag-mehrere-personen.pdf")).not.toHaveLength(0);

    await user.click(screen.getByRole("button", { name: "Importieren" }));

    expect(mutate).toHaveBeenCalledWith("datev-review-ambiguous");
  });
});
