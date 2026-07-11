package com.jasper.documentmatcher.classification;

import java.util.List;

public interface DocumentClassifier {

    ClassificationResult classify(String documentText, List<CategoryCandidate> categories);
}
