import { Typography } from "@mui/material";
import { useParams } from "react-router-dom";
import { PageContainer } from "../../shared/components/PageContainer/PageContainer";

export function EmployeeDetailPage() {
  const { employeeId } = useParams<{ employeeId: string }>();

  return (
    <PageContainer>
      <Typography variant="h5" component="h2">
        Personalakte {employeeId}
      </Typography>
    </PageContainer>
  );
}
