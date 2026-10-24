package io.github.diadalv.pms;

import io.github.diadalv.pms.guest.Guest;
import io.github.diadalv.pms.hotel.Hotel;
import io.github.diadalv.pms.reservation.Reservation;
import io.github.diadalv.pms.room.Room;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        // ===== 1. Στήσιμο ξενοδοχείου (μία φορά) =====
        Hotel hotel = new Hotel("Hotel Rhodes");

        Room room101 = new Room("101", 1, "Single");
        Room room204 = new Room("204", 2, "Double");
        Room room301 = new Room("301", 3, "Suite");

        hotel.addRoom(room101);
        hotel.addRoom(room204);
        hotel.addRoom(room301);

        System.out.println(hotel.getName() + " has " + hotel.getRooms().size() + " rooms");

        // ===== 2. Επισκέπτες =====
        Guest maria = new Guest("Maria", "Papadopoulou", "maria@mail.com", "+306912345678");
        Guest nikos = new Guest("Nikos", "Georgiou", null, null);
        Guest anna = new Guest("Anna", "Ivanova", null, null);
        Guest piet = new Guest("Piet", "de Vries", null, null);

        // ===== 3. Κρατήσεις και διαθεσιμότητα =====
        Reservation mariaStay = hotel.book(maria, room204,
                LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 8));
        System.out.println("Booked: " + mariaStay.getGuest().fullName() + ", " + mariaStay.nights() + " nights");

        System.out.println("204 free 8-10 Nov? " + hotel.isAvailable(room204,
                LocalDate.of(2026, 11, 8), LocalDate.of(2026, 11, 10)));

        System.out.println("204 free 7-9 Nov? " + hotel.isAvailable(room204,
                LocalDate.of(2026, 11, 7), LocalDate.of(2026, 11, 9)));

        try {
            hotel.book(nikos, room204, LocalDate.of(2026, 11, 6), LocalDate.of(2026, 11, 9));
        } catch (IllegalStateException e) {
            System.out.println("Booking failed: " + e.getMessage());
        }

        mariaStay.cancel();
        System.out.println("After Maria cancels, 204 free 6-9 Nov? " + hotel.isAvailable(room204,
                LocalDate.of(2026, 11, 6), LocalDate.of(2026, 11, 9)));

        // ===== 4. Αφίξεις / In-house / Αναχωρήσεις =====
        LocalDate today = LocalDate.of(2026, 11, 5);

        Reservation annaStay = hotel.book(anna, room101, today, today.plusDays(2));
        Reservation pietStay = hotel.book(piet, room301, today.minusDays(3), today);

        pietStay.checkIn();

        System.out.println("Arrivals today: " + hotel.getArrivals(today).size());
        System.out.println("In-house: " + hotel.getInHouse().size());
        System.out.println("Departures today: " + hotel.getDepartures(today).size());

        for (Reservation r : hotel.getArrivals(today)) {
            System.out.println("  Arriving: " + r.getGuest().fullName() + " → room " + r.getRoom().getNumber());
        }


    }
}