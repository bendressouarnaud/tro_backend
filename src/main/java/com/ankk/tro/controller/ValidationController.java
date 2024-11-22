package com.ankk.tro.controller;

import com.ankk.tro.enums.ReservationState;
import com.ankk.tro.model.Pays;
import com.ankk.tro.model.Publication;
import com.ankk.tro.model.Reservation;
import com.ankk.tro.model.Utilisateur;
import com.ankk.tro.repositories.PaysRepository;
import com.ankk.tro.repositories.PublicationRepository;
import com.ankk.tro.repositories.ReservationRepository;
import com.ankk.tro.repositories.UtilisateurRepository;
import com.ankk.tro.services.EmailService;
import com.ankk.tro.services.Firebasemessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.text.DecimalFormat;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
public class ValidationController {

    // A T T R I B U T E S
    private final PublicationRepository publicationRepository;
    private final ReservationRepository reservationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PaysRepository paysRepository;
    private final Firebasemessage firebasemessage;
    private final EmailService emailService;


    // M E T H O D S
    @GetMapping("/validation/{reservationId}")
    public ModelAndView validation(@PathVariable long reservationId) {
        // Find RESERVATION :
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("validation");
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);

        // Find UTILISATEUR :
        Utilisateur utilisateur = reservation.getUtilisateur();
        // Find MONTANT :
        int montant = reservation.getMontant();

        DecimalFormat formatter = new DecimalFormat("###,###,###"); // ###,###,###.00
        String resultAmount = formatter.format(montant);
        modelAndView.addObject(
            "client",
                ("Felicitations " + utilisateur.getNom() + " " +
                utilisateur.getPrenom())
        );
        modelAndView.addObject("montant", (resultAmount + " est effectif !)"));
        modelAndView.addObject("date",
                OffsetDateTime.now(Clock.systemUTC()).
                        truncatedTo(ChronoUnit.SECONDS).
                        format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // persist
        reservation.setReservationState(ReservationState.EFFECTUE);
        reservationRepository.save(reservation);

        // Return response :
        Utilisateur owner = reservation.getPublication().getUtilisateur();

        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("id", owner.getId());
        stringMap.put("nom", owner.getNom());
        stringMap.put("prenom", owner.getPrenom());
        stringMap.put("adresse", owner.getAdresse());
        Pays paysOwner = paysRepository.findById(owner.getPays().getId()).orElse(null);
        stringMap.put("nationnalite", paysOwner.getAbreviation());
        stringMap.put("publicationid", reservation.getPublication().getIdentifiant());

        // Notify the publication's OWNER
        Pays paysSuscriber = paysRepository.findById(utilisateur.getPays().getId()).orElse(null);

        String channel_ID = reservation.getId().toString() +
                utilisateur.getId().toString() +
                owner.getId().toString();

        // Add members :
        emailService.addMembersToChannels(Stream.of(utilisateur, owner).toList(), channel_ID);

        // Notify PUBLICATION's curent suscriber :
        firebasemessage.notifySuscriberAboutPublicationChannelID(utilisateur.getFcmToken(),
                reservation.getPublication().getId().toString(),
                channel_ID);

        // Notify PUBLICATION's owner :
        firebasemessage.notifyOwnerAboutNewReservation(owner,utilisateur,reservation.getPublication(),
                paysSuscriber,
                reservation.getReserve(), channel_ID);

        // Notify SUSCRIBER that PAYMENT has been DONE :
        firebasemessage.notifySuscriberAboutReservationValidation(
                utilisateur,owner,reservation,
                paysOwner.getAbreviation());

        return modelAndView;
    }

    @GetMapping("/confidentialite")
    public ModelAndView confidentialite() {
        // Find RESERVATION :
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("confidentialite");
        return modelAndView;
    }


    @GetMapping("/suppression")
    public ModelAndView suppression() {
        // Find RESERVATION :
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("suppression");
        return modelAndView;
    }

    @PostMapping("/supprimer")
    public ModelAndView supprimer(@RequestParam("email") String email,
        @RequestParam(name="password") String password) {
        Utilisateur ur = utilisateurRepository.
                findByEmailAndPwdAndActive(email, password, 1).orElse(null);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("comptesupprime");
        if(ur != null){
            ur.setActive(0);
            utilisateurRepository.save(ur);
            // Display PAGE :
            modelAndView.addObject(
                    "client",
                    (ur.getNom() + " " +
                            ur.getPrenom())
            );
            modelAndView.addObject(
                    "info",
                    "Votre compte a ete supprime");
        }
        else{
            modelAndView.addObject(
                    "client",
                    "Les identifiants sont incorrects"
            );
            modelAndView.addObject(
                    "info",
                    "...");
        }
        // Just UPDATE
        return modelAndView;
    }


    @GetMapping("/invalidation/{reservationId}")
    public ModelAndView invalidation(@PathVariable long reservationId) {
        // Find RESERVATION :
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("validation");
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);

        // Find UTILISATEUR :
        Utilisateur utilisateur = reservation.getUtilisateur();
        // Find MONTANT :
        int montant = reservation.getMontant();

        DecimalFormat formatter = new DecimalFormat("###,###,###"); // ###,###,###.00
        String resultAmount = formatter.format(montant);
        modelAndView.addObject(
                "client",
                ("Oups " + utilisateur.getNom() + " " +
                        utilisateur.getPrenom())
        );
        modelAndView.addObject("montant", (resultAmount + " n'a pas ete traite !)"));
        modelAndView.addObject("date",
                OffsetDateTime.now(Clock.systemUTC()).
                        truncatedTo(ChronoUnit.SECONDS).
                        format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return modelAndView;
    }

    @GetMapping("/test")
    public ModelAndView test() {
        // Find RESERVATION :
        Pays pays = new Pays();
        pays.setId(1l);
        pays.setLibelle("AZERTY");
        pays.setAbreviation("AZE");
        paysRepository.save(pays);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("test");
        return modelAndView;
    }

}
