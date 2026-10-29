package io.github.diadalv.pms.billing;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ChargeResponse(LocalDate date, ChargeType type, String description, BigDecimal amount) {
    public static ChargeResponse from (Charge charge) {
        return new ChargeResponse (charge.date(), charge.type(), charge.description(), charge.amount());
    }
}
