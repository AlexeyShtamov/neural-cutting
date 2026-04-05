package ru.shtamov.neural_cutting.storage;

public record StoredFileDescriptor(
        String storagePath,
        String originalFileName,
        String contentType,
        long fileSize
) {
}
