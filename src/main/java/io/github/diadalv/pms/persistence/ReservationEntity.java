package io.github.diadalv.pms.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")
public class ReservationEntity {

    @Id
    @Column(name = "reservation_id")
    private String id;

    @Column(name = "guest_first_name")
    private String guestFirstName;

    @Column(name = "guest_last_name")
    private String guestLastName;

    @Column(name = "guest_email")
    private String guestEmail;

    @Column(name = "guest_phone")
    private String guestPhone;

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "departure_date")
    private LocalDate departureDate;

    @Column(name = "reservation_status")
    private String status;

    @Column(name = "rate_per_night")
    private BigDecimal ratePerNight;

    protected ReservationEntity() {
    }

    public ReservationEntity(String id, String guestFirstName, String guestLastName,
                             String guestEmail, String guestPhone, String roomNumber,
                             LocalDate arrivalDate, LocalDate departureDate,
                             String status, BigDecimal ratePerNight) {
        this.id = id;
        this.guestFirstName = guestFirstName;
        this.guestLastName = guestLastName;
        this.guestEmail = guestEmail;
        this.guestPhone = guestPhone;
        this.roomNumber = roomNumber;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.status = status;
        this.ratePerNight = ratePerNight;
    }

    public String getId() { return id; }
    public String getGuestFirstName() { return guestFirstName; }
    public String getGuestLastName() { return guestLastName; }
    public String getGuestEmail() { return guestEmail; }
    public String getGuestPhone() { return guestPhone; }
    public String getRoomNumber() { return roomNumber; }
    public LocalDate getArrivalDate() { return arrivalDate; }
    public LocalDate getDepartureDate() { return departureDate; }
    public String getStatus() { return status; }
    public BigDecimal getRatePerNight() { return ratePerNight; }

    public void setStatus(String status) { this.status = status; }
}