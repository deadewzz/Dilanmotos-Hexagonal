package com.dilanmotos.application.UseCases;

import com.dilanmotos.domain.repository.BackupPort;
import org.springframework.stereotype.Service;

@Service
public class BackupUC {
    
    private final BackupPort backupPort;

    public BackupUC(BackupPort backupPort) {
        this.backupPort = backupPort;
    }

    public byte[] executeBackup() {
        return backupPort.generateSqlBackup();
    }
}