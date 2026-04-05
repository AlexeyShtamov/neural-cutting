package ru.shtamov.neural_cutting.storage;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileStorageService {

    StoredFileDescriptor store(MultipartFile file, UUID ownerId, UUID resumeId, int versionNumber);

    void deleteIfExists(String storagePath);
}
