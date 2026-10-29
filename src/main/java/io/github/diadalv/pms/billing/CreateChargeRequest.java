package io.github.diadalv.pms.billing;

import java.math.BigDecimal;

public record CreateChargeRequest(ChargeType type, String description, BigDecimal amount) { }

