package io.github.diadalv.pms.config;

import io.github.diadalv.pms.hotel.Hotel;
import io.github.diadalv.pms.persistence.JpaReservationRepository;
import io.github.diadalv.pms.persistence.JpaRoomRepository;
import io.github.diadalv.pms.persistence.ReservationJpaRepository;
import io.github.diadalv.pms.persistence.RoomJpaRepository;
import io.github.diadalv.pms.reservation.ReservationRepository;
import io.github.diadalv.pms.room.RoomRepository;
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
    public RoomRepository roomRepository(RoomJpaRepository jpaRepository) {
        return new JpaRoomRepository(jpaRepository);
    }

    @Bean
    public Hotel hotel(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        return new Hotel("Hotel Rhodes", reservationRepository, roomRepository);
    }
}