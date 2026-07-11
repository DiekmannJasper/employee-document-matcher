import { Container, Typography } from "@mui/material";
import { EmployeeDataGrid } from "../../features/employees/components/EmployeeDataGrid";
import { useEmployees } from "../../features/employees/hooks/useEmployees";
import { EmptyState } from "../../shared/components/EmptyState/EmptyState";
import { ErrorState } from "../../shared/components/ErrorState/ErrorState";
import { LoadingState } from "../../shared/components/LoadingState/LoadingState";

export function DashboardPage() {
  const { data: employees, isPending, isError, refetch } = useEmployees();

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h5" component="h2" gutterBottom>
        Mitarbeiter
      </Typography>
      {isPending && <LoadingState message="Mitarbeiter werden geladen…" />}
      {isError && <ErrorState message="Mitarbeiter konnten nicht geladen werden." onRetry={refetch} />}
      {!isPending && !isError && employees.length === 0 && (
        <EmptyState message="Es sind noch keine Mitarbeiter angelegt." />
      )}
      {!isPending && !isError && employees.length > 0 && <EmployeeDataGrid employees={employees} />}
    </Container>
  );
}
