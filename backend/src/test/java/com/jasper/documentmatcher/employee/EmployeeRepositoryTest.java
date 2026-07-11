package com.jasper.documentmatcher.employee;

import static org.assertj.core.api.Assertions.assertThat;

import com.jasper.documentmatcher.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class EmployeeRepositoryTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void loadsTenSyntheticEmployeesFromMigration() {
        assertThat(employeeRepository.count()).isEqualTo(10);
    }

    @Test
    void findsEmployeeByUniquePersonnelNumber() {
        var employee = employeeRepository.findByPersonnelNumber("EMP-1001");

        assertThat(employee)
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getFirstName()).isEqualTo("Anna");
                    assertThat(found.getLastName()).isEqualTo("Müller");
                    assertThat(found.getDepartment()).isEqualTo("Produktentwicklung");
                });
    }
}
