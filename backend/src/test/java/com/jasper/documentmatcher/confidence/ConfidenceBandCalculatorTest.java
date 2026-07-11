package com.jasper.documentmatcher.confidence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ConfidenceBandCalculatorTest {

    private final ConfidenceBandCalculator calculator =
            new ConfidenceBandCalculator(new BigDecimal("0.80"), new BigDecimal("0.40"));

    @Test
    void returnsNoneForANullScore() {
        assertThat(calculator.bandFor(null)).isEqualTo(ConfidenceLevel.NONE);
    }

    @Test
    void returnsHighAtOrAboveTheHighThreshold() {
        assertThat(calculator.bandFor(new BigDecimal("0.80"))).isEqualTo(ConfidenceLevel.HIGH);
        assertThat(calculator.bandFor(new BigDecimal("1.00"))).isEqualTo(ConfidenceLevel.HIGH);
    }

    @Test
    void returnsMediumBetweenTheThresholds() {
        assertThat(calculator.bandFor(new BigDecimal("0.40"))).isEqualTo(ConfidenceLevel.MEDIUM);
        assertThat(calculator.bandFor(new BigDecimal("0.79"))).isEqualTo(ConfidenceLevel.MEDIUM);
    }

    @Test
    void returnsLowBelowTheMediumThreshold() {
        assertThat(calculator.bandFor(new BigDecimal("0.39"))).isEqualTo(ConfidenceLevel.LOW);
        assertThat(calculator.bandFor(BigDecimal.ZERO)).isEqualTo(ConfidenceLevel.LOW);
    }
}
