import { afterEach, describe, expect, it, vi } from "vitest";
import { ApiError, fetchJson } from "./httpClient";

function mockFetchResponse(init: { ok: boolean; status: number; statusText?: string; body?: string }) {
  const { ok, status, statusText = "", body = "" } = init;
  vi.stubGlobal(
    "fetch",
    vi.fn().mockResolvedValue({
      ok,
      status,
      statusText,
      text: () => Promise.resolve(body),
      json: () => (body ? Promise.resolve(JSON.parse(body)) : Promise.reject(new SyntaxError("Unexpected end of JSON input"))),
    }),
  );
}

describe("fetchJson", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("parses a JSON success body", async () => {
    mockFetchResponse({ ok: true, status: 200, body: '{"id":"1"}' });

    await expect(fetchJson<{ id: string }>("/api/x")).resolves.toEqual({ id: "1" });
  });

  it("returns undefined for an empty success body instead of throwing", async () => {
    mockFetchResponse({ ok: true, status: 204 });

    await expect(fetchJson<void>("/api/x")).resolves.toBeUndefined();
  });

  it("throws an ApiError with the ProblemDetail message on failure", async () => {
    mockFetchResponse({ ok: false, status: 400, body: '{"detail":"Nur PDF-Dateien werden unterstützt."}' });

    const error = await fetchJson("/api/x").catch((e: unknown) => e);

    expect(error).toBeInstanceOf(ApiError);
    expect((error as ApiError).message).toBe("Nur PDF-Dateien werden unterstützt.");
    expect((error as ApiError).status).toBe(400);
  });

  it("falls back to the status text when the error body is not JSON", async () => {
    mockFetchResponse({ ok: false, status: 500, statusText: "Internal Server Error" });

    const error = await fetchJson("/api/x").catch((e: unknown) => e);

    expect(error).toBeInstanceOf(ApiError);
    expect((error as ApiError).message).toBe("Internal Server Error");
  });
});
