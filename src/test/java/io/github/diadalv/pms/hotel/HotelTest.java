package io.github.diadalv.pms.hotel;

import io.github.diadalv.pms.guest.Guest;
import io.github.diadalv.pms.reservation.InMemoryReservationRepository;
import io.github.diadalv.pms.reservation.Reservation;
import io.github.diadalv.pms.room.Room;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HotelTest {

    @Test
    void cannotBookRoomForOverlappingDates() {
        // Given: η Μαρία έχει το 204 από 5 έως 8 Νοεμβρίου
        Hotel hotel = new Hotel("Test Hotel", new InMemoryReservationRepository());
        Room room = new Room("204", 2, "Double");
        hotel.addRoom(room);

        Guest maria = new Guest("Maria", "Papadopoulou", null, null);
        Guest nikos = new Guest("Nikos", "Georgiou", null, null);

        hotel.book(maria, room, LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 8), new BigDecimal("120.00"));

        // When + Then: ο Νίκος δεν μπορεί να κλείσει το 204 από 7 έως 9
        assertThrows(IllegalStateException.class, () ->
                hotel.book(nikos, room, LocalDate.of(2026, 11, 7), LocalDate.of(2026, 11, 9), new BigDecimal("120.00"))
        );
    }

    @Test
    void sameDayTurnoverIsAllowed() {
        // Given: στο σύστημα υπάρχει κράτηση της Μαρίας για το 204, από 5 έως 8 Νοεμβρίου
        Hotel hotel = new Hotel("Test Hotel", new InMemoryReservationRepository());
        Room room = new Room("204", 2, "Double");
        hotel.addRoom(room);

        Guest maria = new Guest("Maria", "Vlaxopoulou", null, null);
        hotel.book(maria, room, LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 8), new BigDecimal("120.00"));

        // When: κάποιος ρωτάει αν το 204 είναι ελεύθερο από 8 έως 10 Νοεμβρίου
        boolean available = hotel.isAvailable(room, LocalDate.of(2026, 11, 8), LocalDate.of(2026, 11, 10));

        // Then: το σύστημα απαντάει ναι
        assertTrue(available);
    }
}