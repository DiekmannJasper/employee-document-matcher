package com.jasper.documentmatcher.classification;

import java.util.UUID;

public record CategoryCandidate(UUID categoryId, String code, String displayName) {
}
