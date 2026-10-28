package io.github.diadalv.pms.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ReservationResponse(UUID id, String guestName, String roomNumber, LocalDate arrivalDate, LocalDate departureDate, ReservationStatus status, BigDecimal ratePerNight, BigDecimal balance ) {
    public static ReservationResponse from (Reservation reservation) {
        return new ReservationResponse( reservation.getId() ,reservation.getGuest().fullName(), reservation.getRoom().getNumber(), reservation.getArrivalDate(), reservation.getDepartureDate(), reservation.getStatus(), reservation.getRatePerNight(), reservation.getFolio().getBalance() );

    }
}




