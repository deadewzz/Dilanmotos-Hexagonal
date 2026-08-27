package com.dilanmotos.infrastructure.persistence;

import com.dilanmotos.domain.repository.BackupPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class MysqlBackupAdapter implements BackupPort {

    private final JdbcTemplate jdbcTemplate;

    public MysqlBackupAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public byte[] generateSqlBackup() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("-- Backup Dilan Motos\n");
        sqlBuilder.append("-- Generado dinámicamente desde Java\n\n");
        sqlBuilder.append("SET FOREIGN_KEY_CHECKS = 0;\n\n");

        List<String> tables = jdbcTemplate.queryForList("SHOW TABLES", String.class);

        for (String table : tables) {
            Map<String, Object> createTableMap = jdbcTemplate.queryForMap("SHOW CREATE TABLE `" + table + "`");
            String createTableSql = (String) createTableMap.get("Create Table");
            sqlBuilder.append("DROP TABLE IF EXISTS `").append(table).append("`;\n");
            sqlBuilder.append(createTableSql).append(";\n\n");

            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM `" + table + "`");
            for (Map<String, Object> row : rows) {
                sqlBuilder.append("INSERT INTO `").append(table).append("` VALUES (");
                int i = 0;
                for (Object value : row.values()) {
                    if (i > 0) sqlBuilder.append(", ");
                    if (value == null) {
                        sqlBuilder.append("NULL");
                    } else if (value instanceof Number) {
                        sqlBuilder.append(value);
                    } else if (value instanceof Boolean) {
                        sqlBuilder.append((Boolean) value ? 1 : 0);
                    } else {
                        String escaped = value.toString().replace("'", "''");
                        sqlBuilder.append("'").append(escaped).append("'");
                    }
                    i++;
                }
                sqlBuilder.append(");\n");
            }
            sqlBuilder.append("\n");
        }

        sqlBuilder.append("SET FOREIGN_KEY_CHECKS = 1;\n");
        return sqlBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }
}