package com.jasper.documentmatcher.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LocalDocumentStorageTest {

    @TempDir private Path tempDir;

    @Test
    void storeGeneratesUuidBasedKeyAndPersistsContent() {
        var storage = new LocalDocumentStorage(tempDir.toString());
        var content = new ByteArrayInputStream("%PDF-1.4 test".getBytes(StandardCharsets.UTF_8));

        var storageKey = storage.store(content);

        assertThat(storageKey).matches("[0-9a-f-]{36}\\.pdf");
        assertThat(Files.exists(tempDir.resolve(storageKey))).isTrue();
    }

    @Test
    void loadReturnsPreviouslyStoredContent() throws Exception {
        var storage = new LocalDocumentStorage(tempDir.toString());
        var original = "%PDF-1.4 test content";
        var storageKey = storage.store(new ByteArrayInputStream(original.getBytes(StandardCharsets.UTF_8)));

        try (var loaded = storage.load(storageKey)) {
            var loadedContent = new String(loaded.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(loadedContent).isEqualTo(original);
        }
    }

    @Test
    void deleteRemovesStoredContent() {
        var storage = new LocalDocumentStorage(tempDir.toString());
        var storageKey = storage.store(new ByteArrayInputStream("%PDF-1.4".getBytes(StandardCharsets.UTF_8)));

        storage.delete(storageKey);

        assertThat(Files.exists(tempDir.resolve(storageKey))).isFalse();
    }

    @Test
    void deleteIsIdempotentForUnknownKey() {
        var storage = new LocalDocumentStorage(tempDir.toString());

        assertThatCode(() -> storage.delete(UUID.randomUUID() + ".pdf")).doesNotThrowAnyException();
    }

    @Test
    void rejectsPathTraversalOnLoad() {
        var storage = new LocalDocumentStorage(tempDir.toString());

        assertThatThrownBy(() -> storage.load("../../../../etc/passwd"))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("Invalid storage key");
    }

    @Test
    void rejectsPathTraversalOnDelete() {
        var storage = new LocalDocumentStorage(tempDir.toString());

        assertThatThrownBy(() -> storage.delete("../outside.pdf"))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("Invalid storage key");
    }
}
