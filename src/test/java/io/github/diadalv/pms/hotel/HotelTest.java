package io.github.diadalv.pms.reservation;

import io.github.diadalv.pms.billing.ChargeType;
import io.github.diadalv.pms.billing.Folio;
import io.github.diadalv.pms.guest.Guest;
import io.github.diadalv.pms.room.Room;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

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
        mariasFolio.postCharge(LocalDate.of(2026, 11, 6), ChargeType.EXTRA, "Mini Bar", new BigDecimal("5.00"));

        assertThrows(IllegalStateException.class, () -> mariaReservation.checkOut());
    }
}