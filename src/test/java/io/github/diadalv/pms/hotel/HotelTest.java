package io.github.diadalv.pms.hotel;

import io.github.diadalv.pms.guest.Guest;
import io.github.diadalv.pms.reservation.InMemoryReservationRepository;
import io.github.diadalv.pms.reservation.Reservation;
import io.github.diadalv.pms.room.HousekeepingStatus;
import io.github.diadalv.pms.room.InMemoryRoomRepository;
import io.github.diadalv.pms.room.Room;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class HotelTest {

    @Test
    void cannotBookRoomForOverlappingDates() {
        Hotel hotel = new Hotel("Test Hotel", new InMemoryReservationRepository(), new InMemoryRoomRepository());
        Room room = new Room("204", 2, "Double");
        hotel.addRoom(room);

        Guest maria = new Guest("Maria", "Papadopoulou", null, null);
        Guest nikos = new Guest("Nikos", "Georgiou", null, null);

        hotel.book(maria, room, LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 8), new BigDecimal("120.00"));

        assertThrows(IllegalStateException.class, () ->
                hotel.book(nikos, room, LocalDate.of(2026, 11, 7), LocalDate.of(2026, 11, 9), new BigDecimal("120.00"))
        );
    }

    @Test
    void sameDayTurnoverIsAllowed() {
        Hotel hotel = new Hotel("Test Hotel", new InMemoryReservationRepository(), new InMemoryRoomRepository());
        Room room = new Room("204", 2, "Double");
        hotel.addRoom(room);

        Guest maria = new Guest("Maria", "Vlaxopoulou", null, null);
        hotel.book(maria, room, LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 8), new BigDecimal("120.00"));

        boolean available = hotel.isAvailable(room, LocalDate.of(2026, 11, 8), LocalDate.of(2026, 11, 10));

        assertTrue(available);
    }

    @Test
    void secondNightAuditForSameDateIsRejected() {
        Hotel hotel = new Hotel("Test Hotel", new InMemoryReservationRepository(), new InMemoryRoomRepository());
        Room room = new Room("204", 2, "Double");
        hotel.addRoom(room);

        Guest maria = new Guest("Maria", "Papadopoulou", null, null);

        Reservation mariaReservation = hotel.book(maria, room, LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 8), new BigDecimal("120.00"));
        mariaReservation.checkIn();

        LocalDate auditDate = LocalDate.of(2026, 11, 5);
        hotel.runNightAudit(auditDate);

        assertThrows(IllegalStateException.class, () -> hotel.runNightAudit(auditDate));

        assertEquals(1, mariaReservation.getFolio().getCharges().size());
    }

    @Test
    void roomStatusIsSavedAndLoadedAgain() {
        InMemoryRoomRepository roomRepository = new InMemoryRoomRepository();
        Hotel hotel = new Hotel("Test Hotel", new InMemoryReservationRepository(), roomRepository);
        hotel.addRoom(new Room("101", 1, "Single"));

        hotel.markRoomDirty("101");

        Hotel restartedHotel = new Hotel("Test Hotel", new InMemoryReservationRepository(), roomRepository);
        assertEquals(HousekeepingStatus.DIRTY, restartedHotel.findRoom("101").getHousekeepingStatus());
    }

    @Test
    void checkOutSavesRoomAsDirty() {
        InMemoryRoomRepository roomRepository = new InMemoryRoomRepository();
        Hotel hotel = new Hotel("Test Hotel", new InMemoryReservationRepository(), roomRepository);
        Room room = new Room("204", 2, "Double");
        hotel.addRoom(room);

        Guest maria = new Guest("Maria", "Papadopoulou", null, null);
        Reservation reservation = hotel.book(maria, room, LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 8), new BigDecimal("120.00"));
        hotel.checkIn(reservation.getId());
        hotel.checkOut(reservation.getId());

        Hotel restartedHotel = new Hotel("Test Hotel", new InMemoryReservationRepository(), roomRepository);
        assertEquals(HousekeepingStatus.DIRTY, restartedHotel.findRoom("204").getHousekeepingStatus());
    }
}