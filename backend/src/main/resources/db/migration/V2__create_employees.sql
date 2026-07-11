CREATE TABLE employees (
    id UUID PRIMARY KEY,
    personnel_number VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    department VARCHAR(150) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO employees (id, personnel_number, first_name, last_name, department)
VALUES
    ('10000000-0000-0000-0000-000000000001', 'EMP-1001', 'Anna', 'Müller', 'Produktentwicklung'),
    ('10000000-0000-0000-0000-000000000002', 'EMP-1002', 'David', 'Schneider', 'Vertrieb'),
    ('10000000-0000-0000-0000-000000000003', 'EMP-1003', 'Laura', 'Hoffmann', 'Personal'),
    ('10000000-0000-0000-0000-000000000004', 'EMP-1004', 'Jonas', 'Fischer', 'Finanzen'),
    ('10000000-0000-0000-0000-000000000005', 'EMP-1005', 'Miriam', 'Weber', 'Kundenservice'),
    ('10000000-0000-0000-0000-000000000006', 'EMP-1006', 'Felix', 'Wagner', 'IT-Betrieb'),
    ('10000000-0000-0000-0000-000000000007', 'EMP-1007', 'Sophie', 'Becker', 'Marketing'),
    ('10000000-0000-0000-0000-000000000008', 'EMP-1008', 'Leon', 'Koch', 'Recht'),
    ('10000000-0000-0000-0000-000000000009', 'EMP-1009', 'Nina', 'Richter', 'Produktmanagement'),
    ('10000000-0000-0000-0000-000000000010', 'EMP-1010', 'Tobias', 'Klein', 'Qualitätssicherung');
