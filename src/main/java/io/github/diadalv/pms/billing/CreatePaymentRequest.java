package io.github.diadalv.pms.billing;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreatePaymentRequest(PaymentMethod method, BigDecimal amount) {
}
