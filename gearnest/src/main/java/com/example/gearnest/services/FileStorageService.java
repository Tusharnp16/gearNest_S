package com.example.gearnest.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path rootLocation = Paths.get("uploads");

    public FileStorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location.", e);
        }
    }

    /**
     * Stores a file in a specified subdirectory and returns the generated filename.
     *
     * @param file      The file to store.
     * @param directory The subdirectory to store the file in (e.g.,
     *                  "garage-profiles").
     * @return The unique filename of the stored file.
     * @throws IOException If an I/O error occurs.
     */
    public String storeFile(MultipartFile file, String directory) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Clean the filename
        // Get the original filename, providing an empty string as a default if it's
        // null
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "";
        }

        // Clean the filename (now guaranteed not to be null)
        originalFilename = StringUtils.cleanPath(originalFilename);
        // Generate a new unique filename using UUID
        String generatedFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        // Resolve the target directory and create it if it doesn't exist
        Path targetDirectory = this.rootLocation.resolve(directory);
        if (!Files.exists(targetDirectory)) {
            Files.createDirectories(targetDirectory);
        }

        // Resolve the full target path
        Path targetLocation = targetDirectory.resolve(generatedFilename);

        // Copy the file to the target location
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        }

        return generatedFilename;
    }

    /**
     * Deletes a file from a specified subdirectory.
     *
     * @param filename  The name of the file to delete.
     * @param directory The subdirectory where the file is located.
     */
    public void deleteFile(String filename, String directory) {
        if (filename == null || filename.isBlank() || filename.equals("default.png")) {
            return;
        }

        try {
            Path fileToDelete = this.rootLocation.resolve(directory).resolve(filename).normalize();
            Files.deleteIfExists(fileToDelete);
        } catch (IOException e) {
            System.err.println("Could not delete file: " + filename + ". Error: " + e.getMessage());
        }
    }

    /**
     * Renames an existing file in a specified subdirectory.
     *
     * @param oldFilename The current name of the file.
     * @param newFilename The new name for the file.
     * @param directory   The subdirectory where the file is located.
     * @throws IOException if an I/O error occurs.
     */
    public void renameFile(String oldFilename, String newFilename, String directory) throws IOException {
        if (oldFilename == null || oldFilename.isBlank() || newFilename == null || newFilename.isBlank()) {
            return;
        }

        Path sourcePath = this.rootLocation.resolve(directory).resolve(oldFilename).normalize();
        Path targetPath = this.rootLocation.resolve(directory).resolve(newFilename).normalize();

        if (Files.exists(sourcePath)) {
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Loads a file as a Spring Resource from a specified subdirectory.
     *
     * @param filename  The name of the file to load.
     * @param directory The subdirectory where the file is located.
     * @return The file as a Resource object.
     */
    public Resource loadFileAsResource(String filename, String directory) {
        try {
            Path filePath = this.rootLocation.resolve(directory).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or not readable: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Error loading file: " + filename, ex);
        }
    }
}