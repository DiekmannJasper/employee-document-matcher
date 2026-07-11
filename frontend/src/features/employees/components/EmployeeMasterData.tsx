import { Card, CardContent, Stack, Typography } from "@mui/material";
import type { Employee } from "../api/employeeApi";

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
          <Typography color="text.secondary">Personalnummer: {employee.personnelNumber}</Typography>
          <Typography color="text.secondary">Abteilung: {employee.department}</Typography>
        </Stack>
      </CardContent>
    </Card>
  );
}
