package com.prashanth.book.file;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${application.file.uploads.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(
            @NonNull MultipartFile sourceFile,
            @NonNull Integer userId
    ) {

        if (sourceFile.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file");
        }
        String fileUploadSubPath = "users" + File.separator + userId;
        return uploadFile(sourceFile, fileUploadSubPath);

    }

    private String uploadFile(MultipartFile sourceFile, String fileUploadSubPath) {

        Path uploadDir = Paths.get(fileUploadPath, fileUploadSubPath);

        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            log.error("Failed to create upload directory {}", uploadDir, e);
            throw new IllegalStateException("Could not create upload directory");
        }

        String extension = getFileExtension(sourceFile.getOriginalFilename());
        String filename = extension.isEmpty()
                ? UUID.randomUUID().toString()
                : UUID.randomUUID() + "." + extension;
        Path targetPath = uploadDir.resolve(filename);

        try{
            Files.write(targetPath, sourceFile.getBytes());
            log.info("File saved successfully at {}", targetPath);
            return targetPath.toString();
        } catch (IOException e){
            log.error("Failed to save file {}", filename, e);
            throw new IllegalStateException("Failed to save file");
        }

    }

    private String getFileExtension(String filename) {

        if(filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf(".");
        return (lastDotIndex == -1)
        ? ""
        : filename.substring(lastDotIndex + 1).toLowerCase();

    }

}
