import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { AppProviders } from "../../../app/providers/AppProviders";
import { ApiError } from "../../../shared/api/httpClient";
import * as useUploadDocumentModule from "../hooks/useUploadDocument";
import { UploadDialog } from "./UploadDialog";

vi.mock("../hooks/useUploadDocument");

const useUploadDocument = vi.mocked(useUploadDocumentModule.useUploadDocument);

function idleMutation(overrides: Record<string, unknown> = {}) {
  return {
    mutate: vi.fn(),
    reset: vi.fn(),
    isPending: false,
    isError: false,
    isSuccess: false,
    error: null,
    data: undefined,
    ...overrides,
  };
}

function renderDialog(onClose = vi.fn()) {
  return render(
    <AppProviders>
      <UploadDialog open onClose={onClose} />
    </AppProviders>,
  );
}

function pdfFile(name = "vertrag.pdf") {
  return new File(["%PDF-1.4"], name, { type: "application/pdf" });
}

describe("UploadDialog", () => {
  afterEach(() => {
    vi.resetAllMocks();
  });

  beforeEach(() => {
    useUploadDocument.mockReturnValue(idleMutation() as never);
  });

  it("shows the drop zone when no file is selected", () => {
    useUploadDocument.mockReturnValue(idleMutation() as never);

    renderDialog();

    expect(screen.getByRole("button", { name: "Datei auswählen oder hierher ziehen" })).toBeInTheDocument();
  });

  it("shows the selected file and enables the upload button for a valid PDF", async () => {
    const user = userEvent.setup();
    useUploadDocument.mockReturnValue(idleMutation() as never);

    renderDialog();

    const input = document.querySelector('input[type="file"]') as HTMLInputElement;
    await user.upload(input, pdfFile());

    expect(screen.getByText("vertrag.pdf")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Hochladen" })).toBeEnabled();
  });

  it("shows a validation error and disables upload for an unsupported file", async () => {
    const user = userEvent.setup({ applyAccept: false });
    useUploadDocument.mockReturnValue(idleMutation() as never);

    renderDialog();

    const input = document.querySelector('input[type="file"]') as HTMLInputElement;
    await user.upload(input, new File(["hi"], "notiz.txt", { type: "text/plain" }));

    expect(screen.getByText("Nur PDF-, Word- (.docx) und XML-Dateien werden unterstützt.")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Hochladen" })).toBeDisabled();
  });

  it("shows the selected file and enables the upload button for a valid docx", async () => {
    const user = userEvent.setup();
    useUploadDocument.mockReturnValue(idleMutation() as never);

    renderDialog();

    const input = document.querySelector('input[type="file"]') as HTMLInputElement;
    await user.upload(
      input,
      new File(["PK"], "zeugnis.docx", {
        type: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
      }),
    );

    expect(screen.getByText("zeugnis.docx")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Hochladen" })).toBeEnabled();
  });

  it("triggers the upload mutation when clicking upload", async () => {
    const user = userEvent.setup();
    const mutate = vi.fn();
    useUploadDocument.mockReturnValue(idleMutation({ mutate }) as never);

    renderDialog();

    const input = document.querySelector('input[type="file"]') as HTMLInputElement;
    await user.upload(input, pdfFile());
    await user.click(screen.getByRole("button", { name: "Hochladen" }));

    expect(mutate).toHaveBeenCalledWith(
      expect.objectContaining({ file: expect.any(File) }),
      expect.objectContaining({ onError: expect.any(Function) }),
    );
  });

  it("shows a progress indicator and cancel action while uploading", () => {
    useUploadDocument.mockReturnValue(idleMutation({ isPending: true }) as never);

    renderDialog();

    expect(screen.getByRole("progressbar")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Abbrechen" })).toBeInTheDocument();
  });

  it("shows the server error message when the upload fails", () => {
    useUploadDocument.mockReturnValue(
      idleMutation({ isError: true, error: new ApiError("Nur PDF-Dateien werden unterstützt.", 400) }) as never,
    );

    renderDialog();

    expect(screen.getByText("Nur PDF-Dateien werden unterstützt.")).toBeInTheDocument();
  });

  it("shows a success message and a close action once the upload succeeds", () => {
    useUploadDocument.mockReturnValue(
      idleMutation({
        isSuccess: true,
        data: {
          id: "1",
          originalFilename: "vertrag.pdf",
          status: "UPLOADED",
          uploadedAt: new Date().toISOString(),
          assignedEmployeeName: null,
          assignedCategoryName: null,
        },
      }) as never,
    );

    renderDialog();

    expect(screen.getByText("„vertrag.pdf“ wurde erfolgreich hochgeladen.")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Fertig" })).toBeInTheDocument();
  });

  it("does not close the dialog while an upload is in progress", async () => {
    const user = userEvent.setup();
    const onClose = vi.fn();
    useUploadDocument.mockReturnValue(idleMutation({ isPending: true }) as never);

    renderDialog(onClose);
    await user.click(screen.getByRole("button", { name: "Schließen" }));

    expect(onClose).not.toHaveBeenCalled();
  });

});
