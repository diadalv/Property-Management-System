package io.github.diadalv.pms.billing;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Charge(LocalDate date, ChargeType type, String description, BigDecimal amount) {

    public Charge {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Charge description is required");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Charge amount is required");
        }
    }
}