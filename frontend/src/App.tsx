import { UploadFile } from "@mui/icons-material";
import { Button, Paper, Typography } from "@mui/material";

export function App() {
  return (
    <main className="mx-auto flex min-h-screen max-w-7xl items-start p-6 lg:p-10">
      <Paper className="flex w-full items-center justify-between gap-4 p-6" elevation={0}>
        <Typography component="h1" variant="h5">
          Employee Document Matcher
        </Typography>
        <Button startIcon={<UploadFile />} variant="contained">
          PDF hochladen
        </Button>
      </Paper>
    </main>
  );
}
