package io.github.diadalv.pms.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "charges")
public class ChargeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "folio_id")
    private FolioEntity folio;

    @Column(name = "charge_date")
    private LocalDate date;

    @Column(name = "charge_type")
    private String type;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private BigDecimal amount;

    protected ChargeEntity() {
    }

    public ChargeEntity(LocalDate date, String type, String description, BigDecimal amount) {
        this.date = date;
        this.type = type;
        this.description = description;
        this.amount = amount;
    }

    void setFolio(FolioEntity folio) { this.folio = folio; }

    public LocalDate getDate() { return date; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
}