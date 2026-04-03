package com.grootan.storeflow.service.impl;

import com.grootan.storeflow.exceptions.AppException;
import com.grootan.storeflow.service.FileStorageService;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.storage.dir:uploads}")
    private String storageDir;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(storageDir);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new AppException("Could not initialize storage location", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String saveFile(MultipartFile file, String subDirectory) {
        if (file.isEmpty()) {
            throw new AppException("Failed to store empty file.", HttpStatus.BAD_REQUEST);
        }

        try {
            Path targetDir = rootLocation.resolve(subDirectory);
            Files.createDirectories(targetDir);

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String newFileName = UUID.randomUUID().toString() + extension;
            Path destinationFile = targetDir.resolve(newFileName).normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(targetDir.toAbsolutePath())) {
                throw new AppException("Cannot store file outside current directory.", HttpStatus.BAD_REQUEST);
            }

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            return newFileName;

        } catch (IOException e) {
            throw new AppException("Failed to store file.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String saveAvatar(MultipartFile file, String subDirectory) {
        if (file.isEmpty()) {
            throw new AppException("Failed to store empty file.", HttpStatus.BAD_REQUEST);
        }

        try {
            Path targetDir = rootLocation.resolve(subDirectory);
            Files.createDirectories(targetDir);

            String newFileName = UUID.randomUUID().toString() + ".jpg"; // enforce jpg format for avatars
            Path destinationFile = targetDir.resolve(newFileName).normalize().toAbsolutePath();

            // Use Thumbnailator for resizing
            Thumbnails.of(file.getInputStream())
                    .size(200, 200)
                    .outputFormat("jpg")
                    .toFile(destinationFile.toFile());

            return newFileName;

        } catch (IOException e) {
            throw new AppException("Failed to resize and store avatar.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Resource loadFile(String fileName, String subDirectory) {
        try {
            Path file = rootLocation.resolve(subDirectory).resolve(fileName).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new AppException("Could not read file: " + fileName, HttpStatus.NOT_FOUND);
            }
        } catch (MalformedURLException e) {
            throw new AppException("Could not read file: " + fileName, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void deleteFile(String fileName, String subDirectory) {
        try {
            Path file = rootLocation.resolve(subDirectory).resolve(fileName).normalize();
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new AppException("Failed to delete file: " + fileName, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
