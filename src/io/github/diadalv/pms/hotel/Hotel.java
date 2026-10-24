package io.github.diadalv.pms.hotel;

import io.github.diadalv.pms.guest.Guest;
import io.github.diadalv.pms.reservation.Reservation;
import io.github.diadalv.pms.reservation.ReservationStatus;
import io.github.diadalv.pms.room.Room;
import io.github.diadalv.pms.room.RoomNotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Hotel {
    private final String name;
    private final Map<String, Room> rooms = new LinkedHashMap<>();
    private final List<Reservation> reservations = new ArrayList<>();

    public Hotel(String name) {
        this.name = name;
    }

    public void addRoom(Room room) {
        if (rooms.containsKey(room.getNumber())) {
            throw new IllegalArgumentException("Room " + room.getNumber() + " already exists");
        }
        rooms.put(room.getNumber(), room);
    }

    public boolean isAvailable(Room room, LocalDate arrival, LocalDate departure) {
        for (Reservation reservation : reservations) {
            if (reservation.getRoom() == room
                    && reservation.isActive()
                    && reservation.overlaps(arrival, departure)) {
                return false;
            }
        }
        return true;
    }

    public Reservation book(Guest guest, Room room, LocalDate arrival, LocalDate departure) {
        if (!isAvailable(room, arrival, departure)) {
            throw new IllegalStateException("Room " + room.getNumber() + " is not available for these dates");
        }
        Reservation reservation = new Reservation(guest, room, arrival, departure);
        reservations.add(reservation);
        return reservation;
    }

    public String getName() {
        return name;
    }

    public List<Room> getRooms() {
        return List.copyOf(rooms.values());
    }

    public Room findRoom(String number) {
        Room room = rooms.get(number);
        if (room == null) {
            throw new RoomNotFoundException("Room " + number + " does not exist");
        }
        return room;
    }

    public List<Reservation> getArrivals(LocalDate date) {
        return reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.BOOKED)
                .filter(reservation -> reservation.getArrivalDate().equals(date))
                .toList();
    }

    public List<Reservation> getInHouse() {
        return reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.CHECKED_IN)
                .toList();
    }

    public List<Reservation> getDepartures(LocalDate date) {
        return reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.CHECKED_IN
                        && reservation.getDepartureDate().equals(date))
                .toList();

    }

}