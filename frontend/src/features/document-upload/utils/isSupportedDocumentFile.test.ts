import { describe, expect, it } from "vitest";
import { isSupportedDocumentFile } from "./isSupportedDocumentFile";

describe("isSupportedDocumentFile", () => {
  it("accepts files with the PDF mime type", () => {
    const file = new File(["%PDF-1.4"], "beliebig", { type: "application/pdf" });
    expect(isSupportedDocumentFile(file)).toBe(true);
  });

  it("accepts files with a .pdf extension even without a mime type", () => {
    const file = new File(["%PDF-1.4"], "vertrag.pdf", { type: "" });
    expect(isSupportedDocumentFile(file)).toBe(true);
  });

  it("accepts files with the docx mime type", () => {
    const file = new File(["PK"], "vertrag.docx", {
      type: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    });
    expect(isSupportedDocumentFile(file)).toBe(true);
  });

  it("accepts files with a .docx extension even without a mime type", () => {
    const file = new File(["PK"], "vertrag.docx", { type: "" });
    expect(isSupportedDocumentFile(file)).toBe(true);
  });

  it("accepts files with a .xml extension even without a mime type", () => {
    const file = new File(["<?xml?>"], "export.xml", { type: "" });
    expect(isSupportedDocumentFile(file)).toBe(true);
  });

  it("rejects files that are neither a supported mime type nor a supported extension", () => {
    const file = new File(["hello"], "notiz.txt", { type: "text/plain" });
    expect(isSupportedDocumentFile(file)).toBe(false);
  });
});
