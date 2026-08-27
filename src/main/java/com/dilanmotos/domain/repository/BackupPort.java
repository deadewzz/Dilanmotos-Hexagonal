package com.dilanmotos.domain.repository;

public interface BackupPort {
    byte[] generateSqlBackup();
}