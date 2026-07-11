package com.jasper.documentmatcher.employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    private UUID id;

    @Column(name = "personnel_number", nullable = false, unique = true, length = 20)
    private String personnelNumber;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 150)
    private String department;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Employee() {
    }

    Employee(
            UUID id,
            String personnelNumber,
            String firstName,
            String lastName,
            String department,
            Instant createdAt) {
        this.id = id;
        this.personnelNumber = personnelNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getPersonnelNumber() {
        return personnelNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDepartment() {
        return department;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
