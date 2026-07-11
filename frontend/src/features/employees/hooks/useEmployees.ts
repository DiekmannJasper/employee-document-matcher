import { useQuery } from "@tanstack/react-query";
import { fetchEmployees } from "../api/employeeApi";

export function useEmployees() {
  return useQuery({
    queryKey: ["employees"],
    queryFn: fetchEmployees,
  });
}
