package com.graduation.platform.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Ensure user table has fields required by username/password login.
 */
@Slf4j
@Component
@Order(10)
@RequiredArgsConstructor
public class UserSchemaInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        ensureColumn("user", "username", "VARCHAR(50) DEFAULT NULL COMMENT '用户名'");
        ensureColumn("user", "password", "VARCHAR(255) DEFAULT NULL COMMENT '登录密码(BCrypt)'");
        ensureUniqueIndex("user", "uk_username", "username");
    }

    private void ensureColumn(String tableName, String columnName, String ddl) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                Integer.class,
                tableName,
                columnName
        );

        if (count != null && count == 0) {
            String sql = String.format("ALTER TABLE `%s` ADD COLUMN `%s` %s", tableName, columnName, ddl);
            jdbcTemplate.execute(sql);
            log.info("Added column {}.{}", tableName, columnName);
        }
    }

    private void ensureUniqueIndex(String tableName, String indexName, String columnName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS " +
                        "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND INDEX_NAME = ?",
                Integer.class,
                tableName,
                indexName
        );

        if (count != null && count == 0) {
            Integer duplicateCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM (" +
                            "SELECT `" + columnName + "` FROM `" + tableName + "` " +
                            "WHERE `" + columnName + "` IS NOT NULL AND `" + columnName + "` <> '' " +
                            "GROUP BY `" + columnName + "` HAVING COUNT(*) > 1" +
                            ") t",
                    Integer.class
            );
            if (duplicateCount != null && duplicateCount > 0) {
                log.warn("Skip adding unique index {} on {}.{} because duplicate values exist", indexName, tableName, columnName);
                return;
            }

            String sql = String.format("CREATE UNIQUE INDEX `%s` ON `%s`(`%s`)", indexName, tableName, columnName);
            jdbcTemplate.execute(sql);
            log.info("Added unique index {} on {}.{}", indexName, tableName, columnName);
        }
    }
}
