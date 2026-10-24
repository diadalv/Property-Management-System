package io.github.diadalv.pms.billing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Folio {
    private final List<Charge> charges = new ArrayList<>();
    private final List<Payment> payments = new ArrayList<>();

    public void postCharge(LocalDate date, ChargeType type, String description, BigDecimal amount) {
        charges.add(new Charge(date, type, description, amount));
    }

    public void recordPayment(LocalDate date, PaymentMethod method, BigDecimal amount) {
        payments.add(new Payment(date, method, amount));
    }

    public List<Charge> getCharges() {
        return List.copyOf(charges);
    }

    public List<Payment> getPayments() {
        return List.copyOf(payments);
    }

    public BigDecimal getTotalCharges() {
        BigDecimal total = BigDecimal.ZERO;
        for (Charge charge : charges) {
            total = total.add(charge.amount());
        }
        return total;
    }

    public BigDecimal getTotalPayments() {
        BigDecimal total = BigDecimal.ZERO;
        for (Payment payment : payments) {
            total = total.add(payment.amount());
        }
        return total;
    }

    public BigDecimal getBalance() {
        return getTotalCharges().subtract(getTotalPayments());
    }

    public boolean isSettled() {
        return getBalance().compareTo(BigDecimal.ZERO) == 0;
    }
}