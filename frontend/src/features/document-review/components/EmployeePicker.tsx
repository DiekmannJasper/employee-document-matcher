import { Autocomplete, TextField } from "@mui/material";
import { useMemo } from "react";
import type { Employee } from "../../employees/api/employeeApi";
import { de } from "../../../shared/i18n/de";

interface EmployeePickerProps {
  readonly employees: readonly Employee[];
  readonly selectedEmployeeId: string | null;
  readonly onChange: (employeeId: string | null) => void;
  readonly disabled?: boolean;
}

export function EmployeePicker({ employees, selectedEmployeeId, onChange, disabled = false }: EmployeePickerProps) {
  const selectedEmployee = useMemo(
    () => employees.find((employee) => employee.id === selectedEmployeeId) ?? null,
    [employees, selectedEmployeeId],
  );

  return (
    <Autocomplete
      size="small"
      options={employees}
      value={selectedEmployee}
      onChange={(_event, value) => onChange(value?.id ?? null)}
      getOptionLabel={(employee) => `${employee.firstName} ${employee.lastName} (${employee.personnelNumber})`}
      isOptionEqualToValue={(option, value) => option.id === value.id}
      disabled={disabled}
      renderInput={(params) => <TextField {...params} label={de.review.employeePicker} size="small" />}
    />
  );
}
