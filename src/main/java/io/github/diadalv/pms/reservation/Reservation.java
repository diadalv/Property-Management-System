package io.github.diadalv.pms.reservation;

import io.github.diadalv.pms.billing.ChargeType;
import io.github.diadalv.pms.billing.Folio;
import io.github.diadalv.pms.billing.PaymentMethod;
import io.github.diadalv.pms.guest.Guest;
import io.github.diadalv.pms.room.HousekeepingStatus;
import io.github.diadalv.pms.room.Room;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Reservation {
    private final UUID id;
    private final Guest guest;
    private Room room;
    private final LocalDate arrivalDate;
    private final LocalDate departureDate;
    private ReservationStatus status;
    private final Folio folio = new Folio();
    private final BigDecimal ratePerNight;

    public Reservation(Guest guest, Room room, LocalDate arrivalDate, LocalDate departureDate, BigDecimal ratePerNight) {
        this(UUID.randomUUID(), guest, room, arrivalDate, departureDate, ratePerNight, ReservationStatus.BOOKED);
    }

    private Reservation(UUID id, Guest guest, Room room, LocalDate arrivalDate, LocalDate departureDate,
                        BigDecimal ratePerNight, ReservationStatus status) {
        if (!departureDate.isAfter(arrivalDate)) {
            throw new IllegalArgumentException("Departure must be after arrival");
        }
        this.id = id;
        this.guest = guest;
        this.room = room;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.ratePerNight = ratePerNight;
        this.status = status;
    }

    public static Reservation restore(UUID id, Guest guest, Room room, LocalDate arrivalDate,
                                      LocalDate departureDate, BigDecimal ratePerNight, ReservationStatus status) {
        return new Reservation(id, guest, room, arrivalDate, departureDate, ratePerNight, status);
    }

    public long nights() {
        return ChronoUnit.DAYS.between(arrivalDate, departureDate);
    }

    public Guest getGuest() {
        return guest;
    }
    public Room getRoom() {
        return room;
    }
    public LocalDate getArrivalDate() {
        return arrivalDate;
    }
    public LocalDate getDepartureDate() {
        return departureDate;
    }
    public ReservationStatus getStatus() {
        return status;
    }
    public UUID getId() {
        return id;
    }
    public Folio getFolio() {
        return folio;
    }
    public BigDecimal getRatePerNight() {
        return ratePerNight;
    }

    public void checkIn() {
        requireStatus(ReservationStatus.BOOKED, "check in");
        if (room.getHousekeepingStatus() == HousekeepingStatus.DIRTY) {
            throw new IllegalStateException("Room " + room.getNumber() + " is not ready yet");
        }
        status = ReservationStatus.CHECKED_IN;
    }

    public void checkOut() {
        requireStatus(ReservationStatus.CHECKED_IN, "check out");
        if (!folio.isSettled()) {
            throw new IllegalStateException("Cannot check out: open balance of " + folio.getBalance());
        }
        status = ReservationStatus.CHECKED_OUT;
        room.markDirty();
    }

    public void cancel() {
        requireStatus(ReservationStatus.BOOKED, "cancel");
        status = ReservationStatus.CANCELLED;
    }

    public void markNoShow() {
        requireStatus(ReservationStatus.BOOKED, "mark as no-show");
        status = ReservationStatus.NO_SHOW;
    }

    private void requireStatus(ReservationStatus expected, String action) {
        if (status != expected) {
            throw new IllegalStateException("Cannot " + action + ": reservation is " + status);
        }
    }

    public boolean isActive() {
        return status == ReservationStatus.BOOKED || status == ReservationStatus.CHECKED_IN;
    }

    public boolean overlaps(LocalDate otherArrival, LocalDate otherDeparture) {
        return otherArrival.isBefore(departureDate) && otherDeparture.isAfter(arrivalDate);
    }

    public void postCharge(LocalDate date, ChargeType type, String description, BigDecimal amount) {
         requireStatus(ReservationStatus.CHECKED_IN, "Post Charge");
         folio.postCharge(date, type, description, amount);
    }

    public void recordPayment(LocalDate date, PaymentMethod method, BigDecimal amount) {
        if (status != ReservationStatus.BOOKED && status != ReservationStatus.CHECKED_IN) {
            throw new IllegalStateException("Cannot record payment: reservation is " + status);
        }
        folio.recordPayment(date, method, amount);
    }
}