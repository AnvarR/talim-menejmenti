package com.edu.talim.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String saveFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return uploadDir + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Fayl saqlashda xatolik: " + e.getMessage());
        }
    }

    public void deleteFile(String filePath) {
        try {
            if (filePath != null) {
                Files.deleteIfExists(Paths.get(filePath));
            }
        } catch (IOException e) {
            throw new RuntimeException("Fayl o'chirishda xatolik: " + e.getMessage());
        }
    }
}