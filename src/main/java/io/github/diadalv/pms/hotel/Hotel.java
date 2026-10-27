package io.github.diadalv.pms.hotel;

import io.github.diadalv.pms.billing.ChargeType;
import io.github.diadalv.pms.billing.Folio;
import io.github.diadalv.pms.guest.Guest;
import io.github.diadalv.pms.reservation.Reservation;
import io.github.diadalv.pms.reservation.ReservationRepository;
import io.github.diadalv.pms.reservation.ReservationStatus;
import io.github.diadalv.pms.room.Room;
import io.github.diadalv.pms.room.RoomNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        }

        auditedDates.add(businessDate);
    }

    // ===== Getters =====

    public String getName() {
        return name;
    }
}