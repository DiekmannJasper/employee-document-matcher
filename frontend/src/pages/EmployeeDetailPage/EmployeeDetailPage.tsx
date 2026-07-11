import { Container, Typography } from "@mui/material";
import { useParams } from "react-router-dom";

export function EmployeeDetailPage() {
  const { employeeId } = useParams<{ employeeId: string }>();

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h5" component="h2">
        Personalakte {employeeId}
      </Typography>
    </Container>
  );
}
