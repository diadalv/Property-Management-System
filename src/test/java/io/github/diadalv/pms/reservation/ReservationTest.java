package io.github.diadalv.pms.reservation;

import io.github.diadalv.pms.billing.ChargeType;
import io.github.diadalv.pms.billing.Folio;
import io.github.diadalv.pms.billing.PaymentMethod;
import io.github.diadalv.pms.guest.Guest;
import io.github.diadalv.pms.room.Room;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.github.diadalv.pms.billing.ChargeType.EXTRA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReservationTest {

    @Test
    void cannotCheckOutWithOpenBalance() {
        // Given: η Μαρία είναι checked-in και χρωστάει 5€ από το minibar
        Room room = new Room("204", 2, "Double");
        Guest maria = new Guest("Maria", "Vlaxopoulou", null, null);
        Reservation mariaReservation = new Reservation(maria, room,
                LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 8), new BigDecimal("120.00"));
        mariaReservation.checkIn();

        Folio mariasFolio = mariaReservation.getFolio();
        mariasFolio.postCharge(LocalDate.of(2026, 11, 6), EXTRA, "Mini Bar", new BigDecimal("5.00"));

        // When + Then: το check-out απορρίπτεται
        assertThrows(IllegalStateException.class, () -> mariaReservation.checkOut());
    }

    @Test
    void canCheckOutWhenBalanceIsZero() {
        // Given: η Μαρία είναι checked-in, χρεώθηκε 5.78€ από το minibar και τα πλήρωσε με κάρτα
        Room room = new Room("204", 2, "Double");
        Guest maria = new Guest("Maria", "Vlaxopoulou", null, null);
        Reservation mariaReservation = new Reservation(maria, room,
                LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 8), new BigDecimal("120.00"));
        mariaReservation.checkIn();
        mariaReservation.getFolio().postCharge(LocalDate.of(2026, 11, 6), ChargeType.EXTRA, "Mini bar", new BigDecimal("5.78"));
        mariaReservation.getFolio().recordPayment(LocalDate.of(2026, 11, 6), PaymentMethod.CARD, new BigDecimal("5.78"));

        // When: κάνει check-out
        mariaReservation.checkOut();

        // Then: η κράτηση είναι CHECKED_OUT
        assertEquals(ReservationStatus.CHECKED_OUT, mariaReservation.getStatus());
    }
}