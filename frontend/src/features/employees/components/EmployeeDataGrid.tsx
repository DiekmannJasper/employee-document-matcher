import { DataGrid, type GridColDef } from "@mui/x-data-grid";
import { useMemo } from "react";
import { useNavigate } from "react-router-dom";
import type { Employee } from "../api/employeeApi";
import { de } from "../../../shared/i18n/de";

interface EmployeeDataGridProps {
  readonly employees: readonly Employee[];
}

export function EmployeeDataGrid({ employees }: EmployeeDataGridProps) {
  const navigate = useNavigate();

  const columns = useMemo<GridColDef<Employee>[]>(
    () => [
      {
        field: "name",
        headerName: de.employees.columns.name,
        flex: 1,
        valueGetter: (_value, employee) => `${employee.firstName} ${employee.lastName}`,
      },
      { field: "personnelNumber", headerName: de.employees.columns.personnelNumber, width: 160 },
      { field: "department", headerName: de.employees.columns.department, flex: 1 },
    ],
    [],
  );

  return (
    <DataGrid
      rows={employees}
      columns={columns}
      getRowId={(employee) => employee.id}
      onRowClick={(params) => navigate(`/employees/${params.id}`)}
      disableRowSelectionOnClick
      hideFooterSelectedRowCount
      autoHeight
      sx={{ "& .MuiDataGrid-row": { cursor: "pointer" } }}
    />
  );
}
