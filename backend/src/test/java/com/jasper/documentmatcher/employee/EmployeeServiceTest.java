package com.jasper.documentmatcher.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock private EmployeeRepository employeeRepository;

    @Test
    void findAllMapsEntitiesToResponses() {
        var employee = employee(UUID.randomUUID());
        when(employeeRepository.findAll()).thenReturn(List.of(employee));

        var service = new EmployeeService(employeeRepository);
        var result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().personnelNumber()).isEqualTo(employee.getPersonnelNumber());
    }

    @Test
    void findByIdReturnsMatchingEmployee() {
        var id = UUID.randomUUID();
        var employee = employee(id);
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        var service = new EmployeeService(employeeRepository);
        var result = service.findById(id);

        assertThat(result.id()).isEqualTo(id);
    }

    @Test
    void findByIdThrowsWhenEmployeeIsUnknown() {
        var id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        var service = new EmployeeService(employeeRepository);

        assertThatThrownBy(() -> service.findById(id)).isInstanceOf(EmployeeNotFoundException.class);
    }

    private Employee employee(UUID id) {
        return new Employee(id, "EMP-1001", "Anna", "Müller", "Produktentwicklung", Instant.now());
    }
}
