package com.jasper.documentmatcher.storage;

import java.io.InputStream;

public interface DocumentStorage {

    String store(InputStream content);

    InputStream load(String storageKey);

    void delete(String storageKey);
}
