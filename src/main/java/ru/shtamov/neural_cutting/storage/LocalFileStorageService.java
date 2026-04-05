package ru.shtamov.neural_cutting.storage;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.shtamov.neural_cutting.exception.StorageException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final StorageProperties properties;

    public LocalFileStorageService(StorageProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void init() {
        try {
            Files.createDirectories(rootPath());
        } catch (IOException exception) {
            throw new StorageException("Failed to initialize storage directory", exception);
        }
    }

    @Override
    public StoredFileDescriptor store(MultipartFile file, UUID ownerId, UUID resumeId, int versionNumber) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "resume" : file.getOriginalFilename());
        String extension = extractExtension(originalFileName);
        String storedFileName = UUID.randomUUID() + extension;
        Path targetDirectory = rootPath()
                .resolve(ownerId.toString())
                .resolve(resumeId.toString())
                .resolve("v" + versionNumber);

        try {
            Files.createDirectories(targetDirectory);
            Path targetFile = targetDirectory.resolve(storedFileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return new StoredFileDescriptor(
                    rootPath().relativize(targetFile).toString(),
                    originalFileName,
                    file.getContentType(),
                    file.getSize()
            );
        } catch (IOException exception) {
            throw new StorageException("Failed to store uploaded file", exception);
        }
    }

    @Override
    public void deleteIfExists(String storagePath) {
        if (!StringUtils.hasText(storagePath)) {
            return;
        }

        try {
            Files.deleteIfExists(rootPath().resolve(storagePath));
        } catch (IOException exception) {
            throw new StorageException("Failed to delete stored file: " + storagePath, exception);
        }
    }

    private Path rootPath() {
        return properties.uploadDir().toAbsolutePath().normalize();
    }

    private String extractExtension(String filename) {
        int index = filename.lastIndexOf('.');
        return index >= 0 ? filename.substring(index) : "";
    }
}
