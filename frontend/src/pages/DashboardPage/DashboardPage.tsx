import { Container, Typography } from "@mui/material";

export function DashboardPage() {
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h5" component="h2" gutterBottom>
        Mitarbeiter
      </Typography>
    </Container>
  );
}
