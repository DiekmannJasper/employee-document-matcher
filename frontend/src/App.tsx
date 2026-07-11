import { AppShell } from "./shared/components/AppShell/AppShell";
import { AppRoutes } from "./app/routes/AppRoutes";

export function App() {
  return (
    <AppShell>
      <AppRoutes />
    </AppShell>
  );
}
