package com.ankk.tro.schedulers;

import com.ankk.tro.enums.ReservationState;
import com.ankk.tro.model.Bonus;
import com.ankk.tro.model.Publication;
import com.ankk.tro.model.Reservation;
import com.ankk.tro.model.Utilisateur;
import com.ankk.tro.repositories.*;
import com.ankk.tro.services.Firebasemessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentScheduler {

    // ATTRIBUTES :
    private final PublicationRepository publicationRepository;
    private final ReservationRepository reservationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CodeFiliationRepository codeFiliationRepository;
    private final BonusRepository bonusRepository;
    private final Firebasemessage firebasemessage;
    @Value("${godfather.payment.percentage}")
    private double godfatherPayment;
    @Value("${owner.payment.percentagewithgodfather}")
    private double ownerPaymentWithGodfather;
    @Value("${owner.payment.percentagewithoutgodfather}")
    private double ownerPaymentWithoutGodfather;


    // METHODS
    @Scheduled(cron="0 0 * * * *", zone="Africa/Nouakchott")  // toutes les heures les jours à 9h
    @Async // Execute in a separate thread
    public void execution(){

        // Look for RESERVATION with state TRAITE
        List<Reservation> lesReservations = reservationRepository.
                findAllByReservationStateAndLastUpdateDatetimeGreaterThanEqual(
                ReservationState.TRAITE, OffsetDateTime.now(Clock.systemUTC()));
        lesReservations.forEach( reservation -> {

            Utilisateur suscriber = reservation.getUtilisateur();
            Publication publication = reservation.getPublication();
            Utilisateur owner = publication.getUtilisateur();

            if(publication.getPrix() > 0) {
                if (!suscriber.getCodeInvitation().isEmpty()) {
                    // Look for OWNER GODFATHER :
                    codeFiliationRepository.findByCode(suscriber.getCodeInvitation())
                            .ifPresent(
                                    d -> {
                                        // Create new line in BONUS :
                                        Bonus bonus = new Bonus();
                                        bonus.setMontant((double) reservation.getMontant() * godfatherPayment);
                                        bonus.setUtilisateur(d.getUtilisateur());
                                        bonusRepository.save(bonus);
                                        // SUM UP bonuses
                                        List<Bonus> lesBonus =
                                                bonusRepository.findByUtilisateur(d.getUtilisateur());
                                        double totBonus = lesBonus.stream().mapToDouble(Bonus::getMontant).sum();
                                        // Notify GODFATHER :
                                        firebasemessage.notifyUserAboutBonus(d.getUtilisateur(),
                                                publication.getIdentifiant(),
                                                totBonus);

                                        // Payment to OWNER
                                        Bonus bonusOwner = new Bonus();
                                        bonusOwner.setMontant((double) publication.getPrix() * ownerPaymentWithGodfather);
                                        bonusOwner.setUtilisateur(owner);
                                        bonusRepository.save(bonusOwner);
                                        // SUM UP bonuses
                                        List<Bonus> lesBonusOwner =
                                                bonusRepository.findByUtilisateur(owner);
                                        double totBonusOwner = lesBonusOwner.stream().mapToDouble(Bonus::getMontant).sum();
                                        // Notify OWNER :
                                        firebasemessage.notifyUserAboutBonus(owner,
                                                publication.getIdentifiant(),
                                                totBonusOwner);
                                    }
                            );
                } else {
                    // Payment to OWNER
                    Bonus bonusOwner = new Bonus();
                    bonusOwner.setMontant((double) publication.getPrix() * ownerPaymentWithoutGodfather);
                    bonusOwner.setUtilisateur(owner);
                    bonusRepository.save(bonusOwner);
                    // SUM UP bonuses
                    List<Bonus> lesBonusOwner =
                            bonusRepository.findByUtilisateur(owner);
                    double totBonusOwner = lesBonusOwner.stream().mapToDouble(Bonus::getMontant).sum();
                    // Notify OWNER :
                    firebasemessage.notifyUserAboutBonus(owner,
                            publication.getIdentifiant(),
                            totBonusOwner);
                }
            }

            // Update :
            reservation.setReservationState(ReservationState.RECU);
            reservationRepository.save(reservation);
        });

    }

}
