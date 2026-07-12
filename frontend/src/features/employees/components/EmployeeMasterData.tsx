import { Card, CardContent, Stack, Typography } from "@mui/material";
import type { Employee } from "../api/employeeApi";
import { de } from "../../../shared/i18n/de";

interface EmployeeMasterDataProps {
  readonly employee: Employee;
}

export function EmployeeMasterData({ employee }: EmployeeMasterDataProps) {
  return (
    <Card variant="outlined">
      <CardContent>
        <Typography variant="h5" component="h2">
          {employee.firstName} {employee.lastName}
        </Typography>
        <Stack direction="row" spacing={3} sx={{ mt: 1 }}>
          <Typography color="text.secondary">{de.employees.details.personnelNumber(employee.personnelNumber)}</Typography>
          <Typography color="text.secondary">{de.employees.details.department(employee.department)}</Typography>
        </Stack>
      </CardContent>
    </Card>
  );
}
