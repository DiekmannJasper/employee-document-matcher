package com.jasper.documentmatcher.storage;

import java.io.InputStream;

public interface DocumentStorage {

    String store(InputStream content, String extension);

    InputStream load(String storageKey);

    void delete(String storageKey);
}
