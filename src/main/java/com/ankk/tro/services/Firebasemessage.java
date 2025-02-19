package com.ankk.tro.services;

import com.ankk.tro.enums.SmartphoneType;
import com.ankk.tro.httpbean.UserTokenMobileOs;
import com.ankk.tro.model.*;
import com.ankk.tro.repositories.LocalParametersRepository;
import com.ankk.tro.repositories.VilleRepository;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Firebasemessage {

    // ATTRIBUTES :
    private final EmailService emailService;
    private final VilleRepository villeRepository;
    private final LocalParametersRepository localParametersRepository;
    String typeMessage = "fcm";

    @Async
    public void notifySuscriberAboutCible(List<UserTokenMobileOs> tokens, Publication publication,
                                          String destination, boolean updatedPublication){
        Notification builder = new Notification(publication.getIdentifiant(),
                !updatedPublication ?
                        (destination + "   Réserve : "+ String.valueOf(publication.getReserve()) + " Kg")
                :
                "Annonce modifiée");
        LocalParameters localParameters = localParametersRepository.findById(1L).orElse(null);
        try {
            for(UserTokenMobileOs userToken : tokens){
                if(!userToken.getToken().isBlank()) {
                    if (userToken.getSmartphoneType() == 1) {
                        // ANDROID  :
                        Message me = Message.builder()
                                .setNotification(builder)
                                .setToken(userToken.getToken())
                                .putData("type", typeMessage)
                                .putData("sujet", "1")  // Subject
                                .putData("id", publication.getId().toString())  // Feed 'Magasin' table :
                                .putData("userid", String.valueOf(publication.getUtilisateur().getId()))
                                .putData("villedepart", String.valueOf(publication.getVilleDepart().getId()))
                                .putData("villedestination", String.valueOf(publication.getVilleDestination().getId()))
                                .putData("datevoyage", String.valueOf(publication.getDateVoyage().toString()))
                                .putData("datepublication", String.valueOf(publication.getCreationDatetime().toString()))
                                .putData("reserve", String.valueOf(publication.getReserve()))
                                .putData("identifiant", publication.getIdentifiant())
                                .putData("prix", String.valueOf(publication.getPrix()))
                                .putData("devise", String.valueOf(publication.getDevise().getId()))
                                .build();
                        try {
                            FirebaseMessaging.getInstance().send(me);
                        } catch (FirebaseMessagingException e) {
                            System.out.println("FirebaseMessagingException ANDROID : " + e.getMessage());
                        }
                    }
                    else {
                        // FIRST, SEND APN
                        ApnsConfig apn = ApnsConfig.builder()
                                .setAps(Aps.builder()
                                        .setSound("default")
                                        .putCustomData("content-available", 1)
                                        .build())
                                .putHeader("apns-priority", "5")
                                //.putHeader("apns-push-type","background")
                                .build();

                        Message me = Message.builder()
                                .setNotification(builder)
                                .setApnsConfig(apn)
                                .setToken(userToken.getToken())
                                .putData("type", typeMessage)
                                .putData("sujet", "1")  // Subject
                                .putData("id", publication.getId().toString())  // Feed 'Magasin' table :
                                .putData("userid", String.valueOf(publication.getUtilisateur().getId()))
                                .putData("villedepart", String.valueOf(publication.getVilleDepart().getId()))
                                .putData("villedestination", String.valueOf(publication.getVilleDestination().getId()))
                                .putData("datevoyage", String.valueOf(publication.getDateVoyage().toString()))
                                .putData("datepublication", String.valueOf(publication.getCreationDatetime().toString()))
                                .putData("reserve", String.valueOf(publication.getReserve()))
                                .putData("identifiant", publication.getIdentifiant())
                                .putData("prix", String.valueOf(publication.getPrix()))
                                .putData("devise", String.valueOf(publication.getDevise().getId()))
                                .build();
                        // Add it :
                        try {
                            FirebaseMessaging.getInstance().send(me);
                        } catch (FirebaseMessagingException e) {
                            System.out.println("FirebaseMessagingException ANDROID : " + e.getMessage());
                        }

                        // SECOND :
                    /*Message meS = Message.builder()
                            .setNotification(builder)
                            .setToken(userToken.getToken())
                            .putData("type", typeMessage)
                            .putData("sujet", "1")  // Subject
                            .putData("id", publication.getId().toString())  // Feed 'Magasin' table :
                            .putData("userid", String.valueOf(publication.getUtilisateur().getId()))
                            .putData("villedepart", String.valueOf(publication.getVilleDepart().getId()))
                            .putData("villedestination", String.valueOf(publication.getVilleDestination().getId()))
                            .putData("datevoyage", String.valueOf(publication.getDateVoyage().toString()))
                            .putData("datepublication", String.valueOf(publication.getCreationDatetime().toString()))
                            .putData("reserve", String.valueOf(publication.getReserve()))
                            .putData("identifiant", publication.getIdentifiant())
                            .putData("prix", String.valueOf(publication.getPrix()))
                            .putData("devise", String.valueOf(publication.getDevise().getId()))
                            .build();
                    FirebaseMessaging.getInstance().send(meS);*/

                        // Send a mail
                        StringBuilder contenu = new StringBuilder();
                        contenu.append("<h2> Nouvelle destination </h2>");
                        contenu.append("<p> Date : ");
                        String[] dateHeure = generateDateAndTime(publication.getDateVoyage()).split("T");
                        contenu.append(dateHeure[0]);
                        contenu.append("      Heure : ");
                        contenu.append(dateHeure[1]);
                        contenu.append(" </p>");
                        contenu.append("<p> De : ");
                        contenu.append(villeRepository.findById(publication.getVilleDepart().getId())
                                .orElse(null).getLibelle());
                        contenu.append(" </p>");
                        contenu.append("<p> A : ");
                        contenu.append(villeRepository.findById(publication.getVilleDestination().getId())
                                .orElse(null).getLibelle());
                        contenu.append(" </p>");
                        contenu.append("</h3>");
                        assert localParameters != null;
                        if (localParameters.isEnvoiMail())
                            emailService.mailNotification(userToken.getEmail(), "Nouvelle destination", contenu.toString());
                    }
                }
                else{
                    // Send a mail
                    StringBuilder contenu = new StringBuilder();
                    contenu.append("<h2> Nouvelle destination </h2>");
                    contenu.append("<p> Date : ");
                    String[] dateHeure = generateDateAndTime(publication.getDateVoyage()).split("T");
                    contenu.append(dateHeure[0]);
                    contenu.append("      Heure : ");
                    contenu.append(dateHeure[1]);
                    contenu.append(" </p>");
                    contenu.append("<p> De : ");
                    contenu.append(villeRepository.findById(publication.getVilleDepart().getId())
                            .orElse(null).getLibelle());
                    contenu.append(" </p>");
                    contenu.append("<p> A : ");
                    contenu.append(villeRepository.findById(publication.getVilleDestination().getId())
                            .orElse(null).getLibelle());
                    contenu.append(" </p>");
                    contenu.append("</h3>");
                    assert localParameters != null;
                    if (localParameters.isEnvoiMail())
                        emailService.mailNotification(userToken.getEmail(), "Nouvelle destination", contenu.toString());
                }
            }
        } catch (Exception e) {
            System.out.println("notifySuscriberAboutCible : "+ e.getMessage());
        }
    }


    // Notify OWNER for a new RESERVATION :
    @Async
    public void notifyOwnerAboutNewReservation(Utilisateur owner,
                                               Utilisateur suscriber,
                                               Publication publication,
                                               Pays pays, int reserve,
                                               String channelid)
    {
        Message me = null;
        Notification builder = new Notification(publication.getIdentifiant(),
                ("Souscripteur : " + suscriber.getNom() + " " + suscriber.getPrenom()));
        LocalParameters localParameters = localParametersRepository.findById(1L).orElse(null);
        if(owner.getSmartphoneType() == SmartphoneType.IPHONE) {
            // IPHONE
            ApnsConfig apn =  ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setSound("default")
                            .putCustomData("content-available",1)
                            .build())
                    .putHeader("apns-priority","5")
                    //.putHeader("apns-push-type","background")
                    .build();
            me = Message.builder()
                    .setNotification(builder)
                    .setApnsConfig(apn)
                    .setToken(owner.getFcmToken())
                    .putData("type", typeMessage)
                    .putData("sujet", "2")  // Subject
                    .putData("id", String.valueOf(suscriber.getId()))  // Feed 'Magasin' table :
                    .putData("nationalite", pays.getAbreviation())  // Feed 'Magasin' table :
                    .putData("nom", suscriber.getNom())
                    .putData("prenom", suscriber.getPrenom())
                    .putData("adresse", suscriber.getAdresse())
                    .putData("idpub", String.valueOf(publication.getId()))
                    .putData("reserve", String.valueOf(reserve))
                    .putData("channelid", channelid)
                    .build();
            try {
                FirebaseMessaging.getInstance().send(me);

                // Send a mail
                StringBuilder contenu = new StringBuilder();
                contenu.append("<h2> Nouvelle RESERVATION </h2>");
                contenu.append("<p> Client : ");
                contenu.append(suscriber.getNom()).append(" ").append(suscriber.getPrenom());
                contenu.append("</p>");
                assert localParameters != null;
                if(localParameters.isEnvoiMail()) emailService.mailNotification(owner.getEmail(),
                        "RESERVATION", contenu.toString());
            } catch (Exception e) {
                System.out.println("notifyOwnerAboutNewReservation : " + e.toString());
            }
        }
        else{
            me = Message.builder()
                    .setNotification(builder)
                    .setToken(owner.getFcmToken())
                    .putData("type", typeMessage)
                    .putData("sujet", "2")  // Subject
                    .putData("id", String.valueOf(suscriber.getId()))  // Feed 'Magasin' table :
                    .putData("nationalite", pays.getAbreviation())  // Feed 'Magasin' table :
                    .putData("nom", suscriber.getNom())
                    .putData("prenom", suscriber.getPrenom())
                    .putData("adresse", suscriber.getAdresse())
                    .putData("idpub", String.valueOf(publication.getId()))
                    .putData("reserve", String.valueOf(reserve))
                    .putData("channelid", channelid)
                    .build();
            try {
                FirebaseMessaging.getInstance().send(me);
            } catch (FirebaseMessagingException e) {
                System.out.println("notifyOwnerAboutNewReservation : " + e.toString());
            }
        }
    }


    @Async
    public void notifyOwnerAboutNewChat(Utilisateur receiver, Utilisateur sender, Chat chat)
    {
        String reduceMessage = chat.getMessage().length() < 15 ?
                chat.getMessage() :
                chat.getMessage().substring(0, 14) + "...";
        Notification builder = new Notification((sender.getNom()+" "+sender.getPrenom()),
                reduceMessage);
        Message me = Message.builder()
                .setNotification(builder)
                .setToken(receiver.getFcmToken())
                .putData("type", typeMessage)
                .putData("sujet", "3")  // Subject
                .putData("message", chat.getMessage())  // Feed 'Magasin' table :
                .putData("time",
                        String.valueOf(chat.getCreationDatetime().toInstant().toEpochMilli()))  // Feed 'Magasin' table :
                .putData("idpub", String.valueOf(chat.getPublication().getId()))
                .putData("iduser", String.valueOf(receiver.getId()))
                .putData("sender", String.valueOf(sender.getId()))
                .putData("identifiant", String.valueOf(chat.getIdentifiant()))
                .build();
        try {
            FirebaseMessaging.getInstance().send(me);
        } catch (FirebaseMessagingException e) {
            System.out.println("notifyOwnerAboutNewChat : "+e.toString());
        }
    }


    @Async
    public void notifySuscriberAboutReservationValidation(
        Utilisateur suscriber, Utilisateur owner,
        Reservation reservation, String nationnalite,
        String channelID
    )
    {
        Notification builder = new Notification(
                reservation.getPublication().getIdentifiant(),
                "Paiement effectué");
        LocalParameters localParameters = localParametersRepository.findById(1L).orElse(null);
        if(suscriber.getSmartphoneType() == SmartphoneType.IPHONE) {
            ApnsConfig apn =  ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setSound("default")
                            .putCustomData("content-available",1)
                            .build())
                    .putHeader("apns-priority","5")
                    //.putHeader("apns-push-type","background")
                    .build();
            Message me = Message.builder()
                    .setNotification(builder)
                    .setApnsConfig(apn)
                    .setToken(suscriber.getFcmToken())
                    .putData("type", typeMessage)
                    .putData("sujet", "4")  // Subject
                    .putData("id", String.valueOf(owner.getId()))
                    .putData("nom", owner.getNom())
                    .putData("prenom", owner.getPrenom())
                    .putData("adresse", owner.getAdresse())
                    .putData("nationalite", nationnalite)
                    .putData("publicationid", String.valueOf(reservation.getPublication().getId()))
                    .putData("reservevalide", String.valueOf(reservation.getReserve()))
                    .putData("channelid", channelID)
                    .build();
            try {
                FirebaseMessaging.getInstance().send(me);
            } catch (FirebaseMessagingException e) {
                System.out.println("notifySuscriberAboutReservationValidation iOS : " + e.toString());
            }
        }
        else{
            // BOTH :
            Message me = Message.builder()
                    .setNotification(builder)
                    .setToken(suscriber.getFcmToken())
                    .putData("type", typeMessage)
                    .putData("sujet", "4")  // Subject
                    .putData("id", String.valueOf(owner.getId()))
                    .putData("nom", owner.getNom())
                    .putData("prenom", owner.getPrenom())
                    .putData("adresse", owner.getAdresse())
                    .putData("nationalite", nationnalite)
                    .putData("publicationid", String.valueOf(reservation.getPublication().getId()))
                    .putData("reservevalide", String.valueOf(reservation.getReserve()))
                    .putData("channelid", channelID)
                    .build();
            try {
                FirebaseMessaging.getInstance().send(me);
            } catch (FirebaseMessagingException e) {
                System.out.println("notifySuscriberAboutReservationValidation : " + e.toString());
            }
        }
    }


    @Async
    public void notifySuscriberAboutDelivery(
            Utilisateur suscriber, Publication publication
    )
    {
        Notification builder = new Notification(
                publication.getIdentifiant(),
                "Colis remis au destinataire");
        LocalParameters localParameters = localParametersRepository.findById(1L).orElse(null);
        if(suscriber.getSmartphoneType() == SmartphoneType.IPHONE) {
            ApnsConfig apn =  ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setSound("default")
                            .putCustomData("content-available",1)
                            .build())
                    .putHeader("apns-priority","5")
                    //.putHeader("apns-push-type","background")
                    .build();

            Message me = Message.builder()
                    .setNotification(builder)
                    .setApnsConfig(apn)
                    .setToken(suscriber.getFcmToken())
                    .putData("type", typeMessage)
                    .putData("sujet", "5")  // Subject
                    .putData("idpub", String.valueOf(publication.getId()))
                    .build();
            try {
                FirebaseMessaging.getInstance().send(me);

                // MAIL :
                StringBuilder contenu = new StringBuilder();
                contenu.append("<h2> Nouvelle LIVRAISON </h2>");
                contenu.append("<p> Identifiant Annonce : ");
                contenu.append(publication.getIdentifiant());
                contenu.append("</p>");
                assert localParameters != null;
                if(localParameters.isEnvoiMail()) emailService.mailNotification(suscriber.getEmail(),
                        "LIVRAISON", contenu.toString());
            } catch (FirebaseMessagingException e) {
                System.out.println("notifySuscriberAboutDelivery : " + e.toString());
            }
        }
        else{
            //
            Message me = Message.builder()
                    .setNotification(builder)
                    .setToken(suscriber.getFcmToken())
                    .putData("type", typeMessage)
                    .putData("sujet", "5")  // Subject
                    .putData("idpub", String.valueOf(publication.getId()))
                    .build();
            try {
                FirebaseMessaging.getInstance().send(me);
            } catch (FirebaseMessagingException e) {
                System.out.println("notifySuscriberAboutDelivery : " + e.toString());
            }
        }
    }


    @Async
    public void notifySenderAboutChatReceipt(
            Utilisateur sender, String identifiant
    )
    {
        if(sender.getSmartphoneType() == SmartphoneType.IPHONE) {
            ApnsConfig apn =  ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setSound("default")
                            .putCustomData("content-available",1)
                            .build())
                    .putHeader("apns-priority","5")
                    //.putHeader("apns-push-type","background")
                    .build();

            Message me = Message.builder()
                    //.setNotification(builder)
                    .setToken(sender.getFcmToken())
                    .setApnsConfig(apn)
                    .putData("type", typeMessage)
                    .putData("sujet", "6")  // Subject
                    .putData("identifiant", identifiant)
                    .build();
            try {
                FirebaseMessaging.getInstance().send(me);
            } catch (FirebaseMessagingException e) {
                System.out.println("notifySenderAboutChatReceipt : " + e.toString());
            }
        }

        //
        Message me = Message.builder()
                //.setNotification(builder)
                .setToken(sender.getFcmToken())
                .putData("type", typeMessage)
                .putData("sujet", "6")  // Subject
                .putData("identifiant", identifiant)
                .build();
        try {
            FirebaseMessaging.getInstance().send(me);
        } catch (FirebaseMessagingException e) {
            System.out.println("notifySenderAboutChatReceipt : " + e.toString());
        }
    }

    @Async
    public void notifySuscriberAboutPublicationUpdate(
            Utilisateur suscriber, Publication publication, int nouvellereservation
    )
    {
        Notification builder = new Notification(
                publication.getIdentifiant(),
                "Votre réserve a été modifiée !");
        LocalParameters localParameters = localParametersRepository.findById(1L).orElse(null);
        if(suscriber.getSmartphoneType() == SmartphoneType.IPHONE) {
            ApnsConfig apn =  ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setSound("default")
                            .putCustomData("content-available",1)
                            .build())
                    .putHeader("apns-priority","5")
                    //.putHeader("apns-push-type","background")
                    .build();

            Message me = Message.builder()
                    .setNotification(builder)
                    .setApnsConfig(apn)
                    .setToken(suscriber.getFcmToken())
                    .putData("type", typeMessage)
                    .putData("sujet", "7")  // Subject
                    .putData("idpub", String.valueOf(publication.getId()))
                    .putData("poids", String.valueOf(nouvellereservation))
                    .build();
            try {
                FirebaseMessaging.getInstance().send(me);
            } catch (FirebaseMessagingException e) {
                System.out.println("notifySuscriberAboutPublicationUpdate : " + e.toString());
            }
        }
        else{
            // BOTH
            Message me = Message.builder()
                    .setNotification(builder)
                    .setToken(suscriber.getFcmToken())
                    .putData("type", typeMessage)
                    .putData("sujet", "7")  // Subject
                    .putData("idpub", String.valueOf(publication.getId()))
                    .putData("poids", String.valueOf(nouvellereservation))
                    .build();
            try {
                FirebaseMessaging.getInstance().send(me);
            } catch (FirebaseMessagingException e) {
                System.out.println("notifySuscriberAboutPublicationUpdate : " + e.toString());
            }
        }
    }


    @Async
    public void notifySuscriberAboutPublicationCancellation(
            Utilisateur suscriber, Publication publication
    )
    {
        Notification builder = new Notification(
                publication.getIdentifiant(),
                "Réservation annulée par l'émetteur !");
        LocalParameters localParameters = localParametersRepository.findById(1L).orElse(null);
        if(suscriber.getSmartphoneType() == SmartphoneType.IPHONE) {
            ApnsConfig apn =  ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setSound("default")
                            .putCustomData("content-available",1)
                            .build())
                    .putHeader("apns-priority","5")
                    //.putHeader("apns-push-type","background")
                    .build();

            Message me = Message.builder()
                    .setNotification(builder)
                    .setApnsConfig(apn)
                    .setToken(suscriber.getFcmToken())
                    .putData("type", typeMessage)
                    .putData("sujet", "8")  // Subject
                    .putData("idpub", String.valueOf(publication.getId()))
                    .build();
            try {
                FirebaseMessaging.getInstance().send(me);

                // MAIL :
                StringBuilder contenu = new StringBuilder();
                contenu.append("<h2> Nouvelle Annulation </h2>");
                contenu.append("<p> Identifiant Annonce : ");
                contenu.append(publication.getIdentifiant());
                contenu.append("</p>");
                assert localParameters != null;
                if(localParameters.isEnvoiMail()) emailService.mailNotification(suscriber.getEmail(),
                        "ANNULATION", contenu.toString());

            } catch (FirebaseMessagingException e) {
                System.out.println("notifySuscriberAboutPublicationCancellation : " + e.toString());
            }
        }
        else {
            // both :
            Message me = Message.builder()
                    .setNotification(builder)
                    .setToken(suscriber.getFcmToken())
                    .putData("type", typeMessage)
                    .putData("sujet", "8")  // Subject
                    .putData("idpub", String.valueOf(publication.getId()))
                    .build();
            try {
                FirebaseMessaging.getInstance().send(me);
            } catch (FirebaseMessagingException e) {
                System.out.println("notifySuscriberAboutPublicationCancellation : " + e.toString());
            }
        }
    }

    @Async
    public void notifyOwnerAboutSubscriptionCancellation(
            Utilisateur owner, Publication publication, long idSuscriber
    )
    {
        Notification builder = new Notification(
                publication.getIdentifiant(),
                "Commande résiliée par le souscripteur !");
        LocalParameters localParameters = localParametersRepository.findById(1L).orElse(null);
        if(owner.getSmartphoneType() == SmartphoneType.IPHONE) {
            ApnsConfig apn =  ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setSound("default")
                            .putCustomData("content-available",1)
                            .build())
                    .putHeader("apns-priority","5")
                    //.putHeader("apns-push-type","background")
                    .build();

            Message me = Message.builder()
                    .setNotification(builder)
                    .setApnsConfig(apn)
                    .setToken(owner.getFcmToken())
                    .putData("type", typeMessage)
                    .putData("sujet", "9")  // Subject
                    .putData("idpub", String.valueOf(publication.getId()))
                    .putData("iduser", String.valueOf(idSuscriber))
                    .build();
            try {
                FirebaseMessaging.getInstance().send(me);

                // MAIL :
                StringBuilder contenu = new StringBuilder();
                contenu.append("<h2> Nouvelle Annulation </h2>");
                contenu.append("<p> Identifiant Annonce : ");
                contenu.append(publication.getIdentifiant());
                contenu.append("</p>");
                assert localParameters != null;
                if(localParameters.isEnvoiMail()) emailService.mailNotification(owner.getEmail(),
                        "ANNULATION", contenu.toString());
            } catch (FirebaseMessagingException e) {
                System.out.println("notifySuscriberAboutPublicationCancellation : " + e.toString());
            }
        }
        else {
            Message me = Message.builder()
                    .setNotification(builder)
                    .setToken(owner.getFcmToken())
                    .putData("type", typeMessage)
                    .putData("sujet", "9")  // Subject
                    .putData("idpub", String.valueOf(publication.getId()))
                    .putData("iduser", String.valueOf(idSuscriber))
                    .build();
            try {
                FirebaseMessaging.getInstance().send(me);
            } catch (FirebaseMessagingException e) {
                System.out.println("notifySuscriberAboutPublicationCancellation : " + e.toString());
            }
        }
    }

    @Async
    public void notifyUserAboutBonus(
            Utilisateur user, String publicationId, double montant
    )
    {
        Notification builder = new Notification(
                publicationId,
                "Nouveau BONUS");
        LocalParameters localParameters = localParametersRepository.findById(1L).orElse(null);
        if(user.getSmartphoneType().getValue() == SmartphoneType.IPHONE.getValue()) {
            ApnsConfig apn =  ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setSound("default")
                            .putCustomData("content-available",1)
                            .build())
                    .putHeader("apns-priority","5")
                    //.putHeader("apns-push-type","background")
                    .build();

            Message me = Message.builder()
                    .setNotification(builder)
                    .setApnsConfig(apn)
                    .setToken(user.getFcmToken())
                    .putData("type", typeMessage)
                    .putData("sujet", "10")  // Subject
                    .putData("montant", String.valueOf(montant))
                    .build();
            try {
                FirebaseMessaging.getInstance().send(me);

                // MAIL :
                StringBuilder contenu = new StringBuilder();
                contenu.append("<h2> Modificatioin du Bonus </h2>");
                contenu.append("<p> Le montant est de : ");
                DecimalFormat formatter = new DecimalFormat("###,###,###"); // ###,###,###.00
                String resultAmount = formatter.format(montant);
                contenu.append(resultAmount);
                contenu.append("</p>");
                assert localParameters != null;
                if(localParameters.isEnvoiMail()) emailService.mailNotification(user.getEmail(),
                        "BONUS", contenu.toString());
            } catch (FirebaseMessagingException e) {
                System.out.println("notifyUserAboutBonus : " + e.toString());
            }
        }
        else {
            // both
            Message me = Message.builder()
                    .setNotification(builder)
                    .setToken(user.getFcmToken())
                    .putData("type", typeMessage)
                    .putData("sujet", "10")  // Subject
                    .putData("montant", String.valueOf(montant))
                    .build();
            try {
                FirebaseMessaging.getInstance().send(me);
            } catch (FirebaseMessagingException e) {
                System.out.println("notifyUserAboutBonus : " + e.toString());
            }
        }
    }

    @Async
    public void notifySuscriberAboutPublicationChannelID(
            Utilisateur user, String publicationId, String channelID
    )
    {
        if(user.getSmartphoneType() == SmartphoneType.IPHONE) {

            ApnsConfig apn =  ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setSound("default")
                            .putCustomData("content-available",1)
                            .build())
                    .putHeader("apns-priority","5")
                    //.putHeader("apns-push-type","background")
                    .build();

            Message me = Message.builder()
                    //.setNotification(builder)
                    .setApnsConfig(apn)
                    .setToken(user.getFcmToken())
                    .putData("type", typeMessage)
                    .putData("sujet", "11")  // Subject
                    .putData("idpub", publicationId)
                    .putData("channelid", channelID)
                    .build();
            try {
                FirebaseMessaging.getInstance().send(me);
            } catch (FirebaseMessagingException e) {
                System.out.println("notifySuscriberAboutPublicationChannelID : " + e.toString());
            }
        }
        else{
            Message me = Message.builder()
                    .setToken(user.getFcmToken())
                    .putData("type", typeMessage)
                    .putData("sujet", "11")  // Subject
                    .putData("idpub", publicationId)
                    .putData("channelid", channelID)
                    .build();
            try {
                FirebaseMessaging.getInstance().send(me);
            } catch (FirebaseMessagingException e) {
                System.out.println("notifySuscriberAboutPublicationChannelID : " + e.toString());
            }
        }
    }

    private String generateDateAndTime(OffsetDateTime time){
        String dayeVoyage = time.truncatedTo(ChronoUnit.SECONDS).
                format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String[] tamponDateHour = dayeVoyage.split("T");
        String[] tamponDate = tamponDateHour[0].split("-");
        String newDate = tamponDate[0] + "/" +tamponDate[1] + "/" + tamponDate[2];
        String hour = tamponDateHour[1].substring(0,7);
        return newDate +"T"+hour;
    }
}
