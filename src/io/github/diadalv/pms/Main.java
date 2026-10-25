package io.github.diadalv.pms;

import io.github.diadalv.pms.billing.Charge;
import io.github.diadalv.pms.billing.Folio;
import io.github.diadalv.pms.billing.PaymentMethod;
import io.github.diadalv.pms.guest.Guest;
import io.github.diadalv.pms.hotel.Hotel;
import io.github.diadalv.pms.reservation.InMemoryReservationRepository;
import io.github.diadalv.pms.reservation.Reservation;
import io.github.diadalv.pms.room.Room;
import io.github.diadalv.pms.room.RoomNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        // ===== 1. Στήσιμο ξενοδοχείου =====
        // Η ΜΟΝΗ γραμμή που ξέρει ότι οι κρατήσεις κρατιούνται στη μνήμη:
        Hotel hotel = new Hotel("Hotel Rhodes", new InMemoryReservationRepository());

        Room room101 = new Room("101", 1, "Single");
        Room room204 = new Room("204", 2, "Double");
        Room room301 = new Room("301", 3, "Suite");

        hotel.addRoom(room101);
        hotel.addRoom(room204);
        hotel.addRoom(room301);

        System.out.println(hotel.getName() + " has " + hotel.getRooms().size() + " rooms");

        // ===== 2. Κρατήσεις =====
        LocalDate today = LocalDate.of(2026, 11, 5);

        Guest anna = new Guest("Anna", "Ivanova", null, null);
        Guest maria = new Guest("Maria", "Papadopoulou", "maria@mail.com", null);

        Reservation annaStay = hotel.book(anna, room101, today, today.plusDays(2), new BigDecimal("85.00"));
        Reservation mariaStay = hotel.book(maria, room204, today, today.plusDays(3), new BigDecimal("120.00"));

        // Μόνο η Anna κάνει check-in. Η Maria δεν έχει έρθει ακόμα.
        annaStay.checkIn();

        System.out.println("Arrivals today: " + hotel.getArrivals(today).size());
        System.out.println("In-house: " + hotel.getInHouse().size());

        // ===== 3. Night audit =====
        hotel.runNightAudit(today);
        hotel.runNightAudit(today.plusDays(1));

        try {
            hotel.runNightAudit(today);   // κατά λάθος ξανά!
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        Folio annaFolio = annaStay.getFolio();
        System.out.println("Anna's charges:");
        for (Charge charge : annaFolio.getCharges()) {
            System.out.println("  " + charge.date() + " | " + charge.description() + " | " + charge.amount());
        }
        System.out.println("Anna's balance: " + annaFolio.getBalance());
        System.out.println("Maria's balance: " + mariaStay.getFolio().getBalance());

        // ===== 4. Check-out =====
        try {
            annaStay.checkOut();
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        annaFolio.recordPayment(today.plusDays(2), PaymentMethod.CARD, annaFolio.getBalance());
        System.out.println("Balance after payment: " + annaFolio.getBalance());

        annaStay.checkOut();
        System.out.println("Anna: " + annaStay.getStatus() + ", room 101 is " + room101.getHousekeepingStatus());

        // ===== 5. Αναζήτηση δωματίου =====
        Room found = hotel.findRoom("204");
        System.out.println("Found room " + found.getNumber() + " on floor " + found.getFloor());

        try {
            hotel.findRoom("999");
        } catch (RoomNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }


    }
}