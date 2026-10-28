package io.github.diadalv.pms.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateReservationRequest(String firstName, String lastName, String email, String phone, String roomNumber, LocalDate arrivalDate, LocalDate departureDate, BigDecimal ratePerNight) {
}
