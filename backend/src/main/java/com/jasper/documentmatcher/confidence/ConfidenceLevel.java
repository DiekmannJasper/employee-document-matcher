package com.jasper.documentmatcher.confidence;

/**
 * A qualitative band, not a calibrated probability - deliberately coarse so the UI never implies
 * more precision than the underlying deterministic/rule-based signals actually support.
 */
public enum ConfidenceLevel {
    HIGH,
    MEDIUM,
    LOW,
    NONE
}
