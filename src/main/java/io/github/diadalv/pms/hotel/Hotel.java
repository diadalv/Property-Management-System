package io.github.diadalv.pms.hotel;

import io.github.diadalv.pms.billing.ChargeType;
import io.github.diadalv.pms.billing.Folio;
import io.github.diadalv.pms.billing.PaymentMethod;
import io.github.diadalv.pms.guest.Guest;
import io.github.diadalv.pms.reservation.Reservation;
import io.github.diadalv.pms.reservation.ReservationNotFoundException;
import io.github.diadalv.pms.reservation.ReservationRepository;
import io.github.diadalv.pms.reservation.ReservationStatus;
import io.github.diadalv.pms.room.Room;
import io.github.diadalv.pms.room.RoomNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class Hotel {
    private final String name;
    private final Map<String, Room> rooms = new LinkedHashMap<>();
    private final ReservationRepository reservationRepository;
    private final Set<LocalDate> auditedDates = new HashSet<>();

    public Hotel(String name, ReservationRepository reservationRepository) {
        this.name = name;
        this.reservationRepository = reservationRepository;
    }

    // ===== Δωμάτια =====

    public void addRoom(Room room) {
        if (rooms.containsKey(room.getNumber())) {
            throw new IllegalArgumentException("Room " + room.getNumber() + " already exists");
        }
        rooms.put(room.getNumber(), room);
    }

    public List<Room> getRooms() {
        return List.copyOf(rooms.values());
    }

    public Room findRoom(String number) {
        Room room = rooms.get(number);
        if (room == null) {
            throw new RoomNotFoundException(number);
        }
        return room;
    }

    public Reservation findReservation(UUID id) {
        return getAllReservations().stream()
                .filter(reservation -> reservation.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    // ===== Κρατήσεις =====

    public boolean isAvailable(Room room, LocalDate arrival, LocalDate departure) {
        for (Reservation reservation : reservationRepository.findAll()) {
            if (reservation.getRoom().equals(room)
                    && reservation.isActive()
                    && reservation.overlaps(arrival, departure)) {
                return false;
            }
        }
        return true;
    }

    public Reservation book(Guest guest, Room room, LocalDate arrival, LocalDate departure, BigDecimal ratePerNight) {
        if (!isAvailable(room, arrival, departure)) {
            throw new IllegalStateException("Room " + room.getNumber() + " is not available for these dates");
        }
        Reservation reservation = new Reservation(guest, room, arrival, departure, ratePerNight);
        reservationRepository.save(reservation);
        return reservation;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // ===== Αφίξεις / In-house / Αναχωρήσεις =====

    public List<Reservation> getArrivals(LocalDate date) {
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.BOOKED)
                .filter(reservation -> reservation.getArrivalDate().equals(date))
                .toList();
    }

    public List<Reservation> getInHouse() {
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.CHECKED_IN)
                .toList();
    }

    public List<Reservation> getDepartures(LocalDate date) {
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.CHECKED_IN
                        && reservation.getDepartureDate().equals(date))
                .toList();
    }

    public Reservation checkIn(UUID id) {
        Reservation reservation = findReservation(id);
        reservation.checkIn();
        reservationRepository.save(reservation);
        return reservation;
    }

    public Reservation checkOut(UUID id) {
        Reservation reservation = findReservation(id);
        reservation.checkOut();
        reservationRepository.save(reservation);
        return reservation;
    }

    public Folio postCharge(UUID id, LocalDate date, ChargeType type, String description, BigDecimal amount) {
        Reservation reservation = findReservation(id);
        reservation.postCharge(date, type, description, amount);
        reservationRepository.save(reservation);
        return reservation.getFolio();
    }

    public Folio recordPayment(UUID id, LocalDate date, PaymentMethod method, BigDecimal amount) {
        Reservation reservation = findReservation(id);
        reservation.recordPayment(date, method, amount);
        reservationRepository.save(reservation);
        return reservation.getFolio();
    }

    // ===== Night audit =====

    // ===== Night audit =====

    public void runNightAudit(LocalDate businessDate) {
        if (auditedDates.contains(businessDate)) {
            throw new IllegalStateException("Night audit already run for " + businessDate);
        }

        for (Reservation reservation : getInHouse()) {
            Folio folio = reservation.getFolio();

            if (folio.hasRoomChargeFor(businessDate)) {
                continue;
            }

            folio.postCharge(
                    businessDate,
                    ChargeType.ROOM,
                    "Room " + reservation.getRoom().getNumber() + " - night of " + businessDate,
                    reservation.getRatePerNight()
            );
            reservationRepository.save(reservation);     // ← ΝΕΟ: αποθήκευσε τη χρέωση στη βάση
        }

        auditedDates.add(businessDate);
    }

    // ===== Getters =====

    public String getName() {
        return name;
    }
}

