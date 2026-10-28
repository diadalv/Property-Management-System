package io.github.diadalv.pms.config;

import io.github.diadalv.pms.billing.ChargeType;
import io.github.diadalv.pms.guest.Guest;
import io.github.diadalv.pms.hotel.Hotel;
import io.github.diadalv.pms.reservation.InMemoryReservationRepository;
import io.github.diadalv.pms.reservation.Reservation;
import io.github.diadalv.pms.reservation.ReservationRepository;
import io.github.diadalv.pms.room.Room;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class HotelConfig {

    @Bean
    public ReservationRepository reservationRepository() {
        return new InMemoryReservationRepository();
    }

    @Bean
    public Hotel hotel(ReservationRepository reservationRepository) {
        Hotel hotel = new Hotel("Hotel Rhodes", reservationRepository);
        hotel.addRoom(new Room("101", 1, "Single"));
        hotel.addRoom(new Room("204", 2, "Double"));
        hotel.addRoom(new Room("301", 3, "Suite"));

//        Sample reservations
        Reservation mariaStay = hotel.book(new Guest("Maria", "Papadopoulou", null, null ), hotel.findRoom("204"), LocalDate.of(2026,11,5), LocalDate.of(2026,11,15),new BigDecimal("75.50"));
        mariaStay.checkIn();
        mariaStay.getFolio().postCharge(LocalDate.of(2026,11,6), ChargeType.EXTRA, "Iced Latte", new BigDecimal("4.75"));

        Reservation nikosStay = hotel.book(new Guest("Nikos", "Martakis", "nikos@test.com", "6958777880"), hotel.findRoom("301"), LocalDate.of(2026,11,7), LocalDate.of(2026,11,17),new BigDecimal("355.50"));
        return hotel;

    }
}