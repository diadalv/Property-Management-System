package io.github.diadalv.pms.hotel;

import io.github.diadalv.pms.reservation.ReservationResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;


@RestController
public class HotelController {
    private final Hotel hotel;

    public HotelController(Hotel hotel) {
        this.hotel = hotel;
    }

    @PostMapping("/night-audit")
    public List<ReservationResponse> runNightAudit(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        hotel.runNightAudit(date);
        return hotel.getInHouse().stream().map(reservation -> ReservationResponse.from(reservation)).toList();
    }

}
