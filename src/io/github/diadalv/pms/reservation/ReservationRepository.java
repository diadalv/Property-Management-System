package io.github.diadalv.pms.reservation;

import java.util.List;

public interface ReservationRepository {

    void save(Reservation reservation);

    List<Reservation> findAll();
}