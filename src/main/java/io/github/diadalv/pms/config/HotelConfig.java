package io.github.diadalv.pms.config;

import io.github.diadalv.pms.hotel.Hotel;
import io.github.diadalv.pms.persistence.JpaReservationRepository;
import io.github.diadalv.pms.persistence.ReservationJpaRepository;
import io.github.diadalv.pms.reservation.ReservationRepository;
import io.github.diadalv.pms.room.Room;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class HotelConfig {

    @Bean
    public ReservationRepository reservationRepository(ReservationJpaRepository jpaRepository,
                                                       @Lazy Hotel hotel) {
        return new JpaReservationRepository(jpaRepository, roomNumber -> hotel.findRoom(roomNumber));
    }

    @Bean
    public Hotel hotel(ReservationRepository reservationRepository) {
        Hotel hotel = new Hotel("Hotel Rhodes", reservationRepository);
        hotel.addRoom(new Room("101", 1, "Single"));
        hotel.addRoom(new Room("204", 2, "Double"));
        hotel.addRoom(new Room("301", 3, "Suite"));
        return hotel;
    }
}