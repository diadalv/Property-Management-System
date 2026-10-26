package io.github.diadalv.pms.config;

import io.github.diadalv.pms.hotel.Hotel;
import io.github.diadalv.pms.reservation.InMemoryReservationRepository;
import io.github.diadalv.pms.reservation.ReservationRepository;
import io.github.diadalv.pms.room.Room;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        return hotel;
    }
}