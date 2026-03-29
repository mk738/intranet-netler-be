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
