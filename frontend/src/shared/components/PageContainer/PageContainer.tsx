import { Container } from "@mui/material";
import type { PropsWithChildren } from "react";

export function PageContainer({ children }: PropsWithChildren) {
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      {children}
    </Container>
  );
}
