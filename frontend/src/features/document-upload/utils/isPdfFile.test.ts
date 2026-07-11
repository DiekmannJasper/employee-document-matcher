import { describe, expect, it } from "vitest";
import { isPdfFile } from "./isPdfFile";

describe("isPdfFile", () => {
  it("accepts files with the PDF mime type", () => {
    const file = new File(["%PDF-1.4"], "beliebig", { type: "application/pdf" });
    expect(isPdfFile(file)).toBe(true);
  });

  it("accepts files with a .pdf extension even without a mime type", () => {
    const file = new File(["%PDF-1.4"], "vertrag.pdf", { type: "" });
    expect(isPdfFile(file)).toBe(true);
  });

  it("rejects files that are neither PDF mime type nor .pdf extension", () => {
    const file = new File(["hello"], "notiz.txt", { type: "text/plain" });
    expect(isPdfFile(file)).toBe(false);
  });
});
