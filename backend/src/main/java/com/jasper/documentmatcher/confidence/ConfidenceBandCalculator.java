package com.jasper.documentmatcher.confidence;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfidenceBandCalculator {

    private final BigDecimal highThreshold;
    private final BigDecimal mediumThreshold;

    public ConfidenceBandCalculator(
            @Value("${app.confidence.high-threshold:0.80}") BigDecimal highThreshold,
            @Value("${app.confidence.medium-threshold:0.40}") BigDecimal mediumThreshold) {
        this.highThreshold = highThreshold;
        this.mediumThreshold = mediumThreshold;
    }

    public ConfidenceLevel bandFor(BigDecimal score) {
        if (score == null) {
            return ConfidenceLevel.NONE;
        }
        if (score.compareTo(highThreshold) >= 0) {
            return ConfidenceLevel.HIGH;
        }
        if (score.compareTo(mediumThreshold) >= 0) {
            return ConfidenceLevel.MEDIUM;
        }
        return ConfidenceLevel.LOW;
    }
}
