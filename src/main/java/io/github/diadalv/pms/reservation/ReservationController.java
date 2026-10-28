package io.github.diadalv.pms.reservation;

import io.github.diadalv.pms.guest.Guest;
import io.github.diadalv.pms.hotel.Hotel;
import io.github.diadalv.pms.room.Room;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReservationController {
    private final Hotel hotel;

    public ReservationController(Hotel hotel) {
        this.hotel = hotel;
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> getReservations() {
        return hotel.getAllReservations().stream().map(reservation -> ReservationResponse.from(reservation)).toList();
    }

    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse createReservation(@RequestBody CreateReservationRequest request) {
        Guest guestDetails = new Guest(request.firstName(), request.lastName(), request.email(), request.phone());
        Room room = hotel.findRoom(request.roomNumber());
        Reservation addReservation = hotel.book(guestDetails, room , request.arrivalDate(), request.departureDate(), request.ratePerNight());
        return ReservationResponse.from(addReservation);
    }
}

