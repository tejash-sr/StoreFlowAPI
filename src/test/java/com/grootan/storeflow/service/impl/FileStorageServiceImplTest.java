package com.grootan.storeflow.service.impl;

import com.grootan.storeflow.exceptions.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FileStorageServiceImplTest {

    @InjectMocks
    private FileStorageServiceImpl fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileStorageService, "storageDir", tempDir.toString());
        fileStorageService.init();
    }

    @Test
    void saveFile_whenValid_returnsFilename() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        String result = fileStorageService.saveFile(file, "test");
        assertNotNull(result);
        assertTrue(result.endsWith(".txt"));
        assertTrue(Files.exists(tempDir.resolve("test").resolve(result)));
    }

    @Test
    void saveFile_whenEmpty_throwsException() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", new byte[0]);
        assertThrows(AppException.class, () -> fileStorageService.saveFile(file, "test"));
    }

    @Test
    void loadFile_whenExists_returnsResource() throws IOException {
        Path testDir = tempDir.resolve("test");
        Files.createDirectories(testDir);
        Path testFile = testDir.resolve("test.txt");
        Files.writeString(testFile, "Hello World");

        Resource resource = fileStorageService.loadFile("test.txt", "test");
        assertNotNull(resource);
        assertTrue(resource.exists());
    }

    @Test
    void loadFile_whenNotExists_throwsException() {
        assertThrows(AppException.class, () -> fileStorageService.loadFile("nonexistent.txt", "test"));
    }
}
