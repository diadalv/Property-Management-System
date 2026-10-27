package io.github.diadalv.pms.reservation;

import io.github.diadalv.pms.guest.Guest;
import io.github.diadalv.pms.room.HousekeepingStatus;
import io.github.diadalv.pms.room.Room;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservationResponse(String guest, String room, LocalDate arrivalDate, LocalDate departureDate, ReservationStatus status,  BigDecimal ratePerNight, BigDecimal folio ) {
    public static ReservationResponse from (Reservation reservation) {
        return new ReservationResponse( reservation.getGuest().fullName(), reservation.getRoom().getNumber(), reservation.getArrivalDate(), reservation.getDepartureDate(), reservation.getStatus(), reservation.getRatePerNight(), reservation.getFolio().getBalance() );

    }
}




