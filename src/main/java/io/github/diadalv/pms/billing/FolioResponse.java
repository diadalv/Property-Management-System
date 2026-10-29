package io.github.diadalv.pms.billing;

import java.math.BigDecimal;
import java.util.List;

public record FolioResponse(List<ChargeResponse> charges,
                            List<PaymentResponse> payments,
                            BigDecimal totalCharges,
                            BigDecimal totalPayments,
                            BigDecimal balance) {

    public static FolioResponse from(Folio folio) {
        return new FolioResponse(
                folio.getCharges().stream()
                        .map(charge -> ChargeResponse.from(charge))
                        .toList(),
                folio.getPayments().stream()
                        .map(payment -> PaymentResponse.from(payment))
                        .toList(),
                folio.getTotalCharges(),
                folio.getTotalPayments(),
                folio.getBalance()
        );
    }
}
