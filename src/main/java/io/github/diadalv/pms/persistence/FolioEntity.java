package io.github.diadalv.pms.persistence;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "folios")
public class FolioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private ReservationEntity reservation;

    @Column(name = "window_number")
    private int windowNumber;

    @OneToMany(mappedBy = "folio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChargeEntity> charges = new ArrayList<>();

    @OneToMany(mappedBy = "folio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentEntity> payments = new ArrayList<>();

    protected FolioEntity() {
    }

    public FolioEntity(int windowNumber) {
        this.windowNumber = windowNumber;
    }

    public void addCharge(ChargeEntity charge) {
        charges.add(charge);
        charge.setFolio(this);
    }

    public void addPayment(PaymentEntity payment) {
        payments.add(payment);
        payment.setFolio(this);
    }

    void setReservation(ReservationEntity reservation) { this.reservation = reservation; }

    public Long getId() { return id; }
    public int getWindowNumber() { return windowNumber; }
    public List<ChargeEntity> getCharges() { return charges; }
    public List<PaymentEntity> getPayments() { return payments; }
}