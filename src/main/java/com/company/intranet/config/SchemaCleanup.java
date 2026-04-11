package com.company.intranet.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Drops legacy binary columns that were replaced by Firebase Storage paths.
 * ddl-auto: update never drops columns, so this handles the one-time cleanup.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class SchemaCleanup implements ApplicationRunner {

    private final JdbcTemplate jdbc;

    @Override
    public void run(ApplicationArguments args) {
        drop("employee_avatars",   "data");
        drop("employee_contract",  "data");
        drop("employee_contract",  "content_type");
        drop("employee_cv",        "data");
        drop("employee_cv",        "content_type");
        drop("card_attachments",   "data");
        drop("news_posts",         "cover_image_data");
        drop("news_posts",         "cover_image_type");
        migrateOnboardingItems();
    }

    /**
     * Migrates onboarding_items from enum-based (task) to template-based (task_key + label_sv + sort_order).
     *
     * With ddl-auto=update, Hibernate cannot add NOT NULL columns to a non-empty table.
     * This method detects that case, clears the stale rows (they are re-created on demand
     * by OnboardingService.getChecklist), and adds the missing columns so the app can start.
     * Idempotent — once the columns exist this is a no-op.
     */
    private void migrateOnboardingItems() {
        if (columnExists("onboarding_items", "task_key")) {
            drop("onboarding_items", "task");
            return;
        }

        log.info("SchemaCleanup: migrating onboarding_items to template-based schema");
        try {
            jdbc.execute("TRUNCATE TABLE onboarding_items");
            jdbc.execute("ALTER TABLE onboarding_items ADD COLUMN IF NOT EXISTS task_key   VARCHAR(60)");
            jdbc.execute("ALTER TABLE onboarding_items ADD COLUMN IF NOT EXISTS label_sv   VARCHAR(120)");
            jdbc.execute("ALTER TABLE onboarding_items ADD COLUMN IF NOT EXISTS sort_order INTEGER DEFAULT 0");
            drop("onboarding_items", "task");
            log.info("SchemaCleanup: onboarding_items migration complete");
        } catch (Exception e) {
            log.warn("SchemaCleanup: onboarding_items migration failed: {}", e.getMessage());
        }
    }

    private boolean columnExists(String table, String column) {
        try {
            Integer count = jdbc.queryForObject(
                    "SELECT count(*) FROM information_schema.columns " +
                    "WHERE table_name = ? AND column_name = ?",
                    Integer.class, table, column);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void drop(String table, String column) {
        try {
            jdbc.execute("ALTER TABLE " + table + " DROP COLUMN IF EXISTS " + column);
            log.debug("SchemaCleanup: dropped {}.{} (if existed)", table, column);
        } catch (Exception e) {
            log.warn("SchemaCleanup: could not drop {}.{}: {}", table, column, e.getMessage());
        }
    }
}
