import { fetchJson } from "../../../shared/api/httpClient";

export interface Employee {
  readonly id: string;
  readonly personnelNumber: string;
  readonly firstName: string;
  readonly lastName: string;
  readonly department: string;
}

export function fetchEmployees(): Promise<Employee[]> {
  return fetchJson<Employee[]>("/api/employees");
}

export function fetchEmployeeById(employeeId: string): Promise<Employee> {
  return fetchJson<Employee>(`/api/employees/${employeeId}`);
}
