import { Container, Typography } from "@mui/material";
import { useMemo } from "react";
import { EmployeeDataGrid } from "../../features/employees/components/EmployeeDataGrid";
import { useEmployees } from "../../features/employees/hooks/useEmployees";
import { useGlobalSearch } from "../../shared/search/useGlobalSearch";
import { EmptyState } from "../../shared/components/EmptyState/EmptyState";
import { ErrorState } from "../../shared/components/ErrorState/ErrorState";
import { LoadingState } from "../../shared/components/LoadingState/LoadingState";

export function DashboardPage() {
  const { data: employees, isPending, isError, refetch } = useEmployees();
  const { query } = useGlobalSearch();

  const filteredEmployees = useMemo(() => {
    if (!employees) {
      return employees;
    }

    const normalizedQuery = query.trim().toLowerCase();
    if (!normalizedQuery) {
      return employees;
    }

    return employees.filter((employee) =>
      `${employee.firstName} ${employee.lastName} ${employee.personnelNumber} ${employee.department}`
        .toLowerCase()
        .includes(normalizedQuery),
    );
  }, [employees, query]);

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h5" component="h2" gutterBottom>
        Mitarbeiter
      </Typography>
      {isPending && <LoadingState message="Mitarbeiter werden geladen…" />}
      {isError && <ErrorState message="Mitarbeiter konnten nicht geladen werden." onRetry={refetch} />}
      {!isPending && !isError && filteredEmployees?.length === 0 && (
        <EmptyState
          message={query ? "Keine Mitarbeiter gefunden." : "Es sind noch keine Mitarbeiter angelegt."}
        />
      )}
      {!isPending && !isError && filteredEmployees && filteredEmployees.length > 0 && (
        <EmployeeDataGrid employees={filteredEmployees} />
      )}
    </Container>
  );
}
