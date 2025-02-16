package com.ankk.tro.repositories;

import com.ankk.tro.enums.ReservationState;
import com.ankk.tro.model.Publication;
import com.ankk.tro.model.Reservation;
import com.ankk.tro.model.Utilisateur;
import org.springframework.data.repository.CrudRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    Reservation findByUtilisateur(Utilisateur user);
    Reservation findByUtilisateurAndPublication(Utilisateur user, Publication publication);
    Reservation findByUtilisateurAndPublicationAndReservationState(Utilisateur user, Publication publication,
                                                                   ReservationState reservationState);
    List<Reservation> findAllByPublication(Publication publication);
    List<Reservation> findAllByPublicationAndReservationState(Publication publication,
                                                              ReservationState reservationState);
    List<Reservation> findAllByReservationStateAndLastUpdateDatetimeGreaterThanEqual(
            ReservationState reservationState, OffsetDateTime datevoyage
    );
}
