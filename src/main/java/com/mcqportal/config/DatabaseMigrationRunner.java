package com.mcqportal.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DatabaseMigrationRunner implements CommandLineRunner {
    private final JdbcTemplate jdbcTemplate;

    public DatabaseMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        jdbcTemplate.execute("ALTER TABLE results ADD COLUMN IF NOT EXISTS category VARCHAR(80)");
        jdbcTemplate.execute("ALTER TABLE results ADD COLUMN IF NOT EXISTS malpractice_detected BOOLEAN NOT NULL DEFAULT FALSE");
        jdbcTemplate.execute("ALTER TABLE results ADD COLUMN IF NOT EXISTS malpractice_warnings INT NOT NULL DEFAULT 0");
        jdbcTemplate.execute("ALTER TABLE results ADD COLUMN IF NOT EXISTS malpractice_reason VARCHAR(255)");
    }
}
