package com.grootan.storeflow.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

public interface FileStorageService {
    String saveFile(MultipartFile file, String subDirectory);
    Resource loadFile(String fileName, String subDirectory);
    void deleteFile(String fileName, String subDirectory);
    
    String saveAvatar(MultipartFile file, String subDirectory);
}
