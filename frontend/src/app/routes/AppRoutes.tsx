import { Route, Routes } from "react-router-dom";
import { DashboardPage } from "../../pages/DashboardPage/DashboardPage";
import { EmployeeDetailPage } from "../../pages/EmployeeDetailPage/EmployeeDetailPage";
import { ReviewPage } from "../../pages/ReviewPage/ReviewPage";

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<DashboardPage />} />
      <Route path="/employees/:employeeId" element={<EmployeeDetailPage />} />
      <Route path="/reviews" element={<ReviewPage />} />
    </Routes>
  );
}
