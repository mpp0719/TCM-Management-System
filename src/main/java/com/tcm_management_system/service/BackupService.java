package com.tcm_management_system.service;

import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

@Service
public class BackupService {

    private static final String BACKUP_DIR = "./backups";
    private static final int MAX_BACKUPS_TO_KEEP = 24; // 12 hours of history at 30-min intervals

    private final JdbcTemplate jdbcTemplate;

    public BackupService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(fixedRate = 30 * 60 * 1000) // every 30 minutes, in milliseconds
    public void scheduledBackup() {
        runBackup();
    }

    @EventListener(ContextClosedEvent.class)
    public void onShutdown() {
        runBackup();
    }

    private void runBackup() {
        try {
            File dir = new File(BACKUP_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss"));
            String filePath = BACKUP_DIR + "/clinicdb-" + timestamp + ".zip";

            jdbcTemplate.execute("BACKUP TO '" + filePath + "'");
            System.out.println("Backup created: " + filePath);

            cleanupOldBackups(dir);
        } catch (Exception e) {
            System.err.println("Backup failed: " + e.getMessage());
        }
    }

    private void cleanupOldBackups(File dir) {
        File[] files = dir.listFiles((d, name) -> name.startsWith("clinicdb-") && name.endsWith(".zip"));
        if (files == null || files.length <= MAX_BACKUPS_TO_KEEP) {
            return;
        }
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));
        int toDelete = files.length - MAX_BACKUPS_TO_KEEP;
        for (int i = 0; i < toDelete; i++) {
            files[i].delete();
        }
    }
}