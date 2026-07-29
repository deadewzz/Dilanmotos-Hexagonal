package com.dilanmotos.infrastructure.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/backup")
@CrossOrigin(origins = "*")
public class BackupController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadBackup() {
        StringBuilder sqlDump = new StringBuilder();

        try (Connection conn = dataSource.getConnection()) {
            sqlDump.append("-- Backup Dilan Motos\n");
            sqlDump.append("-- Fecha: ").append(new Date()).append("\n\n");
            sqlDump.append("SET FOREIGN_KEY_CHECKS=0;\n\n");

            DatabaseMetaData metaData = conn.getMetaData();
            String catalog = conn.getCatalog();
            
            // Obtener la lista de todas las tablas
            ResultSet tables = metaData.getTables(catalog, null, "%", new String[]{"TABLE"});

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                
                sqlDump.append("-- --------------------------------------------------------\n");
                sqlDump.append("-- Estructura y datos para la tabla `").append(tableName).append("`\n");
                sqlDump.append("-- --------------------------------------------------------\n\n");

                // 1. OBTENER ESTRUCTURA (CREATE TABLE)
                sqlDump.append("DROP TABLE IF EXISTS `").append(tableName).append("`;\n");
                try (Statement stmtCreateTable = conn.createStatement();
                     ResultSet rsCreateTable = stmtCreateTable.executeQuery("SHOW CREATE TABLE `" + tableName + "`")) {
                    if (rsCreateTable.next()) {
                        String createTableSql = rsCreateTable.getString(2); // La 2da columna contiene la sentencia DDL
                        sqlDump.append(createTableSql).append(";\n\n");
                    }
                }

                // 2. OBTENER DATOS (INSERT INTO)
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM `" + tableName + "`")) {

                    ResultSetMetaData rsMetaData = rs.getMetaData();
                    int columnCount = rsMetaData.getColumnCount();

                    while (rs.next()) {
                        sqlDump.append("INSERT INTO `").append(tableName).append("` VALUES (");
                        for (int i = 1; i <= columnCount; i++) {
                            Object value = rs.getObject(i);
                            if (value == null) {
                                sqlDump.append("NULL");
                            } else if (value instanceof Number) {
                                sqlDump.append(value);
                            } else {
                                String valStr = value.toString().replace("'", "''").replace("\\", "\\\\");
                                sqlDump.append("'").append(valStr).append("'");
                            }
                            if (i < columnCount) sqlDump.append(", ");
                        }
                        sqlDump.append(");\n");
                    }
                    sqlDump.append("\n\n");
                }
            }

            sqlDump.append("SET FOREIGN_KEY_CHECKS=1;\n");

            byte[] backupBytes = sqlDump.toString().getBytes(StandardCharsets.UTF_8);
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(backupBytes));

            String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "backup_dilanmotos_" + date + ".sql";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(backupBytes.length)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}