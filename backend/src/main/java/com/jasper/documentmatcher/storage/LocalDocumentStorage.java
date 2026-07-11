package com.jasper.documentmatcher.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalDocumentStorage implements DocumentStorage {

    private final Path baseDirectory;

    public LocalDocumentStorage(@Value("${app.storage.local-path:./data/documents}") String localPath) {
        this.baseDirectory = Path.of(localPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(baseDirectory);
        } catch (IOException e) {
            throw new StorageException("Could not create storage directory", e);
        }
    }

    @Override
    public String store(InputStream content) {
        var storageKey = UUID.randomUUID() + ".pdf";
        var target = resolveWithinBaseDirectory(storageKey);

        try {
            Files.copy(content, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Could not store document", e);
        }

        return storageKey;
    }

    @Override
    public InputStream load(String storageKey) {
        var target = resolveWithinBaseDirectory(storageKey);

        try {
            return Files.newInputStream(target);
        } catch (IOException e) {
            throw new StorageException("Could not load document: " + storageKey, e);
        }
    }

    @Override
    public void delete(String storageKey) {
        var target = resolveWithinBaseDirectory(storageKey);

        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            throw new StorageException("Could not delete document: " + storageKey, e);
        }
    }

    private Path resolveWithinBaseDirectory(String storageKey) {
        var resolved = baseDirectory.resolve(storageKey).normalize();

        if (!resolved.startsWith(baseDirectory)) {
            throw new StorageException("Invalid storage key: " + storageKey);
        }

        return resolved;
    }
}
