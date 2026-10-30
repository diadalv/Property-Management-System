package io.github.diadalv.pms.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "folio_id")
    private FolioEntity folio;

    @Column(name = "payment_date")
    private LocalDate date;

    @Column(name = "payment_method")
    private String method;

    @Column(name = "amount")
    private BigDecimal amount;

    protected PaymentEntity() {
    }

    public PaymentEntity(LocalDate date, String method, BigDecimal amount) {
        this.date = date;
        this.method = method;
        this.amount = amount;
    }

    void setFolio(FolioEntity folio) { this.folio = folio; }

    public LocalDate getDate() { return date; }
    public String getMethod() { return method; }
    public BigDecimal getAmount() { return amount; }
}