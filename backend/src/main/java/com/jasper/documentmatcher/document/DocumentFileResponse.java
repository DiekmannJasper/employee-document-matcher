package com.jasper.documentmatcher.document;

import java.io.InputStream;

record DocumentFileResponse(String filename, String contentType, InputStream content) {
}
