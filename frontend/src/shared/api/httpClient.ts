export class ApiError extends Error {
  constructor(
    message: string,
    readonly status: number,
  ) {
    super(message);
    this.name = "ApiError";
  }
}

export async function fetchJson<T>(url: string, init?: RequestInit): Promise<T> {
  const response = await fetch(url, init);

  if (!response.ok) {
    const problem = await response.json().catch(() => null);
    throw new ApiError(problem?.detail ?? response.statusText, response.status);
  }

  return (await response.json()) as T;
}
