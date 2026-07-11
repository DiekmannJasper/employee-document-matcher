package com.jasper.documentmatcher.classification;

import java.util.List;

record CategoryRule(String code, String displayName, List<String> keywords) {
}
