package com.cts.service;

import java.io.IOException;
import java.nio.file.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cts.exception.FileStorageException;
import com.cts.exception.InvalidFileException;

@Service
public class FileStorageService {

    private static final String BASE_DIR = "uploads/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    public String storeFile(MultipartFile file, String subFolder) {
        validateFile(file);
        try {
            Path uploadPath = Paths.get(BASE_DIR + subFolder);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            // Timestamp prefix prevents filename collisions
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.toString();
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + e.getMessage());
        }
    }

    public byte[] loadFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new FileStorageException("File not found at path: " + filePath);
            }
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new FileStorageException("Failed to read file: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File cannot be empty.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
            throw new InvalidFileException("Only PDF files are allowed.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("File size cannot exceed 10 MB.");
        }
    }
}