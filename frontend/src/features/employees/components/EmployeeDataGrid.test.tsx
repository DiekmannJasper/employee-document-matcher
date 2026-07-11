import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { describe, expect, it } from "vitest";
import type { Employee } from "../api/employeeApi";
import { EmployeeDataGrid } from "./EmployeeDataGrid";

const employees: Employee[] = [
  {
    id: "10000000-0000-0000-0000-000000000001",
    personnelNumber: "EMP-1001",
    firstName: "Anna",
    lastName: "Müller",
    department: "Produktentwicklung",
  },
];

function renderGrid() {
  return render(
    <MemoryRouter initialEntries={["/"]}>
      <Routes>
        <Route path="/" element={<EmployeeDataGrid employees={employees} />} />
        <Route path="/employees/:employeeId" element={<div>Personalakte geöffnet</div>} />
      </Routes>
    </MemoryRouter>,
  );
}

describe("EmployeeDataGrid", () => {
  it("renders employee name, personnel number and department", () => {
    renderGrid();

    expect(screen.getByText("Anna Müller")).toBeInTheDocument();
    expect(screen.getByText("EMP-1001")).toBeInTheDocument();
    expect(screen.getByText("Produktentwicklung")).toBeInTheDocument();
  });

  it("navigates to the employee detail page on row click", async () => {
    const user = userEvent.setup();
    renderGrid();

    await user.click(screen.getByText("Anna Müller"));

    expect(screen.getByText("Personalakte geöffnet")).toBeInTheDocument();
  });
});
