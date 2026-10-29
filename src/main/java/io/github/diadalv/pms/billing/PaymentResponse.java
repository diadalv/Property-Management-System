package io.github.diadalv.pms.billing;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentResponse(LocalDate date, PaymentMethod method, BigDecimal amount) {
    public static PaymentResponse from (Payment payment) {
        return new PaymentResponse(payment.date(), payment.method(), payment.amount());
    }
}
