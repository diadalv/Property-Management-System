package io.github.diadalv.pms.reservation;

import io.github.diadalv.pms.billing.ChargeResponse;
import io.github.diadalv.pms.billing.CreateChargeRequest;
import io.github.diadalv.pms.billing.CreatePaymentRequest;
import io.github.diadalv.pms.billing.FolioResponse;
import io.github.diadalv.pms.guest.Guest;
import io.github.diadalv.pms.hotel.Hotel;
import io.github.diadalv.pms.room.Room;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    @GetMapping("/reservations/{id}")
    public ReservationResponse getReservation(@PathVariable UUID id) {return ReservationResponse.from(hotel.findReservation(id));}

    @PostMapping ("/reservations/{id}/check-in")
    public ReservationResponse checkIn(@PathVariable UUID id) {
        Reservation reservation = hotel.findReservation(id);
        reservation.checkIn();
        return ReservationResponse.from(reservation);
    }

    @PostMapping("/reservations/{id}/check-out")
    public ReservationResponse checkOut(@PathVariable UUID id) {
        Reservation reservation = hotel.findReservation(id);
        reservation.checkOut();
        return ReservationResponse.from(reservation);
    }

    @GetMapping("/reservations/{id}/folio")
    public FolioResponse getFolio(@PathVariable UUID id) {return FolioResponse.from(hotel.findReservation(id).getFolio());}

    @PostMapping ("/reservations/{id}/charges")
    @ResponseStatus(HttpStatus.CREATED)
    public FolioResponse postCharge(@PathVariable UUID id, @RequestBody CreateChargeRequest chargeRequest) {
        Reservation reservation = hotel.findReservation(id);
        reservation.postCharge(LocalDate.now(), chargeRequest.type(), chargeRequest.description(), chargeRequest.amount());
        return FolioResponse.from(reservation.getFolio());
    }

    @PostMapping ("/reservations/{id}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public FolioResponse postPayment(@PathVariable UUID id, @RequestBody CreatePaymentRequest paymentRequest) {
        Reservation reservation = hotel.findReservation(id);
        reservation.recordPayment(LocalDate.now(), paymentRequest.method(), paymentRequest.amount());
        return FolioResponse.from(reservation.getFolio());
    }
}

