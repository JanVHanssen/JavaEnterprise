package com.example.application.csv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component("FileWatcher")
public class FileWatcher implements CommandLineRunner {

    @Value("${csv.folder.path}")
    private String csvFolderPath;

    @Autowired
    private CsvImporter csvImporter;

    @Override
    public void run(String... args) throws Exception {
        Path path = Paths.get(csvFolderPath);
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new IOException("Directory not found: " + csvFolderPath);
        }

        System.out.println("Watching directory: " + path.toString());

        WatchService watchService = FileSystems.getDefault().newWatchService();
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
        System.out.println("Watch service registered for directory: " + path.toString());

        // Using an ExecutorService to handle the WatchService in a separate thread
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            System.out.println("Watch service thread started");
            while (true) {
                WatchKey key;
                try {
                    System.out.println("Waiting for key to be signaled...");
                    key = watchService.take(); // Block until events are available
                } catch (InterruptedException e) {
                    System.out.println("Watch service interrupted");
                    return;
                }

                System.out.println("WatchKey received: " + key);
                boolean foundNewFiles = false; // Flag to track if new files are found
                for (WatchEvent<?> event : key.pollEvents()) {
                    System.out.println("Event kind: " + event.kind());
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();
                    System.out.println("Detected file event: " + event.kind() + " for file: " + fileName);
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        System.out.println("New file detected: " + fileName);
                        csvImporter.importCsv(path.resolve(fileName).toString());
                        foundNewFiles = true; // Set flag to true if a new file is found
                    }
                }
                if (!foundNewFiles) {
                    System.out.println("No new files found in the CSV folder.");
                }
                boolean valid = key.reset();
                if (!valid) {
                    System.out.println("WatchKey is no longer valid.");
                    break;
                }
            }
            System.out.println("Watch service thread ending");
        });

        // Ensure the application does not exit immediately
        executorService.shutdown();
    }
}