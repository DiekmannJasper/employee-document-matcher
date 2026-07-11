import { useQuery } from "@tanstack/react-query";
import { fetchEmployeeDocuments } from "../api/employeeDocumentApi";

export function useEmployeeDocuments(employeeId: string | undefined) {
  return useQuery({
    queryKey: ["employee-documents", employeeId],
    queryFn: () => fetchEmployeeDocuments(employeeId as string),
    enabled: Boolean(employeeId),
  });
}
