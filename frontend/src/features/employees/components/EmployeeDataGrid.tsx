import { DataGrid, type GridColDef } from "@mui/x-data-grid";
import { useMemo } from "react";
import { useNavigate } from "react-router-dom";
import type { Employee } from "../api/employeeApi";

interface EmployeeDataGridProps {
  readonly employees: readonly Employee[];
}

export function EmployeeDataGrid({ employees }: EmployeeDataGridProps) {
  const navigate = useNavigate();

  const columns = useMemo<GridColDef<Employee>[]>(
    () => [
      {
        field: "name",
        headerName: "Name",
        flex: 1,
        valueGetter: (_value, employee) => `${employee.firstName} ${employee.lastName}`,
      },
      { field: "personnelNumber", headerName: "Personalnummer", width: 160 },
      { field: "department", headerName: "Abteilung", flex: 1 },
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
