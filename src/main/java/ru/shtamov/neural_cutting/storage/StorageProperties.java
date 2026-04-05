package ru.shtamov.neural_cutting.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(Path uploadDir) {
}
