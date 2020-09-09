package com.lk.taskmanager.configuration;

import com.lk.taskmanager.utils.exceptions.LKAppException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public final class StorageConfig {

    private String directoryName;
    private Path defaultStorageLocation;

    private StorageConfig() {

    }

    protected StorageConfig(String storageDirectory) {
        this.directoryName = storageDirectory;
    }

    protected void initStorage(){
        this.defaultStorageLocation = Paths.get(directoryName).toAbsolutePath().normalize();

        File dir = new File(this.defaultStorageLocation.toString());
        if (!dir.exists()) {
            log.info("Initializing directory for raw data storage");
            try {
                Files.createDirectories(this.defaultStorageLocation);
            } catch (Exception ex) {
                log.info("Storage directory initialization failed");
                throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
            }
        }
    }

//    public Path getDefaultStorage() {
//        return this.defaultStorageLocation;
//    }

    public Path getUserDirectory(String username){;

        Path targetLocation = this.defaultStorageLocation.resolve(username);
        File userDirectory = new File(targetLocation.toString());
        if (!userDirectory.exists()) {
            log.info("Initializing directory for user: ", username);
            try {
                Files.createDirectories(targetLocation);
            } catch (Exception ex) {
                log.info("Storage directory initialization failed for user: ", username);
                throw new LKAppException("Could not create the directory for user", ex);
            }
        }
        return targetLocation;
    }

}
