package com.jasper.documentmatcher;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class PostgresIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void appliesFlywayMigrationsToPostgres() {
        var jdbcTemplate = new JdbcTemplate(dataSource);

        var schemaVersion = jdbcTemplate.queryForObject(
                "SELECT metadata_value FROM application_metadata WHERE metadata_key = ?",
                String.class,
                "schema_version");
        var failedMigrationCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE NOT success",
                Integer.class);
        var appliedMigrationCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success",
                Integer.class);

        assertThat(schemaVersion).isEqualTo("1");
        assertThat(failedMigrationCount).isZero();
        assertThat(appliedMigrationCount).isPositive();
    }
}
