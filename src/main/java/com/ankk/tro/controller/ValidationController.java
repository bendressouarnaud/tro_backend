package com.ankk.tro.controller;

import com.ankk.tro.model.Pays;
import com.ankk.tro.model.Reservation;
import com.ankk.tro.model.Utilisateur;
import com.ankk.tro.repositories.PaysRepository;
import com.ankk.tro.repositories.PublicationRepository;
import com.ankk.tro.repositories.ReservationRepository;
import com.ankk.tro.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.text.DecimalFormat;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Controller
@RequiredArgsConstructor
public class ValidationController {

    // A T T R I B U T E S
    private final PublicationRepository publicationRepository;
    private final ReservationRepository reservationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PaysRepository paysRepository;


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
