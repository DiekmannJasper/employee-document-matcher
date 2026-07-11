import { Route, Routes } from "react-router-dom";
import { DashboardPage } from "../../pages/DashboardPage/DashboardPage";
import { EmployeeDetailPage } from "../../pages/EmployeeDetailPage/EmployeeDetailPage";

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<DashboardPage />} />
      <Route path="/employees/:employeeId" element={<EmployeeDetailPage />} />
    </Routes>
  );
}
