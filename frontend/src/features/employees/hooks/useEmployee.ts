import { useQuery } from "@tanstack/react-query";
import { fetchEmployeeById } from "../api/employeeApi";

export function useEmployee(employeeId: string | undefined) {
  return useQuery({
    queryKey: ["employees", employeeId],
    queryFn: () => fetchEmployeeById(employeeId as string),
    enabled: Boolean(employeeId),
  });
}
