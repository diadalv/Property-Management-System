package io.github.diadalv.pms.config;

import io.github.diadalv.pms.billing.ChargeType;
import io.github.diadalv.pms.guest.Guest;
import io.github.diadalv.pms.hotel.Hotel;
import io.github.diadalv.pms.persistence.RoomEntity;
import io.github.diadalv.pms.persistence.RoomJpaRepository;
import io.github.diadalv.pms.reservation.Reservation;
import io.github.diadalv.pms.reservation.ReservationRepository;
import io.github.diadalv.pms.room.Room;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class SampleDataLoader implements ApplicationRunner {

    private final Hotel hotel;
    private final RoomJpaRepository roomJpaRepository;
    private final ReservationRepository reservationRepository;

    public SampleDataLoader(Hotel hotel, RoomJpaRepository roomJpaRepository,
                            ReservationRepository reservationRepository) {
        this.hotel = hotel;
        this.roomJpaRepository = roomJpaRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        // 1. Τα δωμάτια στη βάση (μόνο όσα λείπουν)
        for (Room room : hotel.getRooms()) {
            if (!roomJpaRepository.existsById(room.getNumber())) {
                roomJpaRepository.save(new RoomEntity(room.getNumber(), room.getFloor(),
                        room.getType(), room.getHousekeepingStatus().name()));
            }
        }

        // 2. Κρατήσεις-παράδειγμα, ΜΟΝΟ αν δεν υπάρχει καμία
        if (!hotel.getAllReservations().isEmpty()) {
            return;
        }

        Reservation mariaStay = hotel.book(new Guest("Maria", "Papadopoulou", null, null),
                hotel.findRoom("204"), LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 15),
                new BigDecimal("75.50"));
        mariaStay.checkIn();
        mariaStay.getFolio().postCharge(LocalDate.of(2026, 11, 6), ChargeType.EXTRA,
                "Iced Latte", new BigDecimal("4.75"));
        reservationRepository.save(mariaStay);

        hotel.book(new Guest("Nikos", "Martakis", "nikos@test.com", "6958777880"),
                hotel.findRoom("301"), LocalDate.of(2026, 11, 7), LocalDate.of(2026, 11, 17),
                new BigDecimal("355.50"));
    }
}