package io.github.diadalv.pms.reservation;

import io.github.diadalv.pms.hotel.Hotel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
