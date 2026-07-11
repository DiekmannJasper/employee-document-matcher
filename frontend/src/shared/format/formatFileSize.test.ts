import { describe, expect, it } from "vitest";
import { formatFileSize } from "./formatFileSize";

describe("formatFileSize", () => {
  it("formats bytes below 1 KB as bytes", () => {
    expect(formatFileSize(512)).toBe("512 B");
  });

  it("formats kilobytes with one decimal place", () => {
    expect(formatFileSize(2048)).toBe("2.0 KB");
  });

  it("formats megabytes with one decimal place", () => {
    expect(formatFileSize(5 * 1024 * 1024)).toBe("5.0 MB");
  });
});
