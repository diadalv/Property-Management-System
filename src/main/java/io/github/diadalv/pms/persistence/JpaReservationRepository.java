package io.github.diadalv.pms.persistence;

import io.github.diadalv.pms.reservation.Reservation;
import io.github.diadalv.pms.reservation.ReservationRepository;
import io.github.diadalv.pms.room.Room;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

public class JpaReservationRepository implements ReservationRepository {

    private final ReservationJpaRepository jpaRepository;
    private final ReservationMapper mapper = new ReservationMapper();
    private final Function<String, Room> findRoom;

    public JpaReservationRepository(ReservationJpaRepository jpaRepository, Function<String, Room> findRoom) {
        this.jpaRepository = jpaRepository;
        this.findRoom = findRoom;
    }

    @Override
    @Transactional
    public void save(Reservation reservation) {
        jpaRepository.findById(reservation.getId().toString())
                .ifPresentOrElse(
                        existing -> mapper.updateEntity(existing, reservation),
                        () -> jpaRepository.save(mapper.toEntity(reservation))
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        return jpaRepository.findAll().stream()
                .map(entity -> mapper.toDomain(entity, findRoom))
                .toList();
    }
}