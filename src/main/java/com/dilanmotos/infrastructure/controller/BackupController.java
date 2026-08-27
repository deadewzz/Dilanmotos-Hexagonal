package com.dilanmotos.infrastructure.controller;

import com.dilanmotos.application.UseCases.BackupUC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/backup")
public class BackupController {

    private final BackupUC backupUC;

    public BackupController(BackupUC backupUC) {
        this.backupUC = backupUC;
    }

    @GetMapping("/download")
    @PreAuthorize("hasRole('ADMIN')") // Ajusta el rol según tu esquema de Spring Security
    public ResponseEntity<byte[]> downloadBackup() {
        byte[] backupData = backupUC.executeBackup();
        String filename = "backup_dilanmotos_" + LocalDate.now() + ".sql";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(backupData);
    }
}