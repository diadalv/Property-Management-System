package io.github.diadalv.pms.billing;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Payment(LocalDate date, PaymentMethod method, BigDecimal amount) {

    public Payment {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
    }
}