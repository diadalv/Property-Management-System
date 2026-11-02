package io.github.diadalv.pms.persistence;

import io.github.diadalv.pms.billing.Charge;
import io.github.diadalv.pms.billing.ChargeType;
import io.github.diadalv.pms.billing.Payment;
import io.github.diadalv.pms.billing.PaymentMethod;
import io.github.diadalv.pms.guest.Guest;
import io.github.diadalv.pms.reservation.Reservation;
import io.github.diadalv.pms.reservation.ReservationStatus;
import io.github.diadalv.pms.room.Room;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class ReservationMapper {

    public ReservationEntity toEntity(Reservation reservation) {
        ReservationEntity entity = new ReservationEntity(
                reservation.getId().toString(),
                reservation.getGuest().firstName(),
                reservation.getGuest().lastName(),
                reservation.getGuest().email(),
                reservation.getGuest().phone(),
                reservation.getRoom().getNumber(),
                reservation.getArrivalDate(),
                reservation.getDepartureDate(),
                reservation.getStatus().name(),
                reservation.getRatePerNight()
        );

        FolioEntity folio = new FolioEntity(1);
        for (Charge charge : reservation.getFolio().getCharges()) {
            folio.addCharge(new ChargeEntity(charge.date(), charge.type().name(),
                    charge.description(), charge.amount()));
        }
        for (Payment payment : reservation.getFolio().getPayments()) {
            folio.addPayment(new PaymentEntity(payment.date(), payment.method().name(),
                    payment.amount()));
        }
        entity.addFolio(folio);

        return entity;
    }

    public void updateEntity(ReservationEntity entity, Reservation reservation) {
        entity.setStatus(reservation.getStatus().name());

        FolioEntity folio = entity.getFolios().get(0);

        List<Charge> charges = reservation.getFolio().getCharges();
        for (int i = folio.getCharges().size(); i < charges.size(); i++) {
            Charge charge = charges.get(i);
            folio.addCharge(new ChargeEntity(charge.date(), charge.type().name(),
                    charge.description(), charge.amount()));
        }

        List<Payment> payments = reservation.getFolio().getPayments();
        for (int i = folio.getPayments().size(); i < payments.size(); i++) {
            Payment payment = payments.get(i);
            folio.addPayment(new PaymentEntity(payment.date(), payment.method().name(),
                    payment.amount()));
        }
    }


    public Reservation toDomain(ReservationEntity entity, Function<String, Room> findRoom) {
        Guest guest = new Guest(entity.getGuestFirstName(), entity.getGuestLastName(),
                entity.getGuestEmail(), entity.getGuestPhone());

        Reservation reservation = Reservation.restore(
                UUID.fromString(entity.getId()),
                guest,
                findRoom.apply(entity.getRoomNumber()),
                entity.getArrivalDate(),
                entity.getDepartureDate(),
                entity.getRatePerNight(),
                ReservationStatus.valueOf(entity.getStatus())
        );

        for (FolioEntity folio : entity.getFolios()) {
            for (ChargeEntity charge : folio.getCharges()) {
                reservation.getFolio().postCharge(charge.getDate(),
                        ChargeType.valueOf(charge.getType()), charge.getDescription(), charge.getAmount());
            }
            for (PaymentEntity payment : folio.getPayments()) {
                reservation.getFolio().recordPayment(payment.getDate(),
                        PaymentMethod.valueOf(payment.getMethod()), payment.getAmount());
            }
        }

        return reservation;
    }
}