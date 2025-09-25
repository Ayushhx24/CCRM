package edu.ccrm.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class Backup_service {

    private static final String DATA_DIRECTORY = "data";
    private static final String BACKUP_ROOT_DIRECTORY = "backups";

    public Path performBackup() throws IOException {
        Path sourceDir = Paths.get(DATA_DIRECTORY);
        Path backupRootDir = Paths.get(BACKUP_ROOT_DIRECTORY);
        Files.createDirectories(backupRootDir);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        Path backupDir = backupRootDir.resolve("backup_" + timestamp);
        Files.createDirectory(backupDir);

        try (Stream<Path> files = Files.list(sourceDir)) {
            files.forEach(file -> {
                try {
                    Files.copy(file, backupDir.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException("Could not copy file: " + file, e);
                }
            });
        }

        System.out.println("Backup created successfully at: " + backupDir);
        return backupDir;
    }
    public long calculateDirectorySize(Path directory) throws IOException {
        try (Stream<Path> walk = Files.walk(directory)) {
            return walk
                .filter(Files::isRegularFile)
                .mapToLong(p -> {
                    try {
                        return Files.size(p);
                    } catch (IOException e) {
                        return 0L;
                    }
                })
                .sum();
        }
    }
}