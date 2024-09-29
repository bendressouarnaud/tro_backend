package com.ankk.tro.services;

import com.ankk.tro.model.*;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class Firebasemessage {

    @Async
    public void notifySuscriberAboutCible(List<String> tokens, Publication publication, String destination){
        Notification builder = new Notification(publication.getIdentifiant(),
                (destination + "   Réserve : "+ String.valueOf(publication.getReserve()) + " Kg"));
        List<Message> listeMessage = new ArrayList<>();
        try {
            for(String token : tokens){
                Message me = Message.builder()
                        .setNotification(builder)
                        .setToken(token)
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
                FirebaseMessaging.getInstance().send(me);
                //listeMessage.add(me);
                //FirebaseMessaging.getInstance().sendAll(listeMessage);
            }
        } catch (FirebaseMessagingException e) {
            System.out.println("notifySuscriberAboutCible : "+e.toString());
        }
    }


    // Notify OWNER for a new RESERVATION :
    @Async
    public void notifyOwnerAboutNewReservation(Utilisateur owner,
                                               Utilisateur suscriber,
                                               Publication publication,
                                               Pays pays, int reserve)
    {
        Notification builder = new Notification(publication.getIdentifiant(),
                ("Souscripteur : " + suscriber.getNom() + " " + suscriber.getPrenom()));
        Message me = Message.builder()
                .setNotification(builder)
                .setToken(owner.getFcmToken())
                .putData("sujet", "2")  // Subject
                .putData("id", String.valueOf(suscriber.getId()))  // Feed 'Magasin' table :
                .putData("nationalite", pays.getAbreviation())  // Feed 'Magasin' table :
                .putData("nom", suscriber.getNom())
                .putData("prenom", suscriber.getPrenom())
                .putData("adresse", suscriber.getAdresse())
                .putData("idpub", String.valueOf(publication.getId()))
                .putData("reserve", String.valueOf(reserve))
                .build();
        try {
            FirebaseMessaging.getInstance().send(me);
        } catch (FirebaseMessagingException e) {
            System.out.println("notifyOwnerAboutNewReservation : "+e.toString());
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
        Reservation reservation, String nationnalite
    )
    {
        Notification builder = new Notification(
                reservation.getPublication().getIdentifiant(),
                "Paiement effectué");
        Message me = Message.builder()
                .setNotification(builder)
                .setToken(suscriber.getFcmToken())
                .putData("sujet", "4")  // Subject
                .putData("id", String.valueOf(owner.getId()))
                .putData("nom", owner.getNom())
                .putData("prenom", owner.getPrenom())
                .putData("adresse", owner.getAdresse())
                .putData("nationalite", nationnalite)
                .putData("publicationid", String.valueOf(reservation.getPublication().getId()))
                .putData("reservevalide", String.valueOf(reservation.getReserve()))
                .build();
        try {
            FirebaseMessaging.getInstance().send(me);
        } catch (FirebaseMessagingException e) {
            System.out.println("notifyOwnerAboutNewReservation : "+e.toString());
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
        Message me = Message.builder()
                .setNotification(builder)
                .setToken(suscriber.getFcmToken())
                .putData("sujet", "5")  // Subject
                .putData("idpub", String.valueOf(publication.getId()))
                .build();
        try {
            FirebaseMessaging.getInstance().send(me);
        } catch (FirebaseMessagingException e) {
            System.out.println("notifyOwnerAboutNewReservation : "+e.toString());
        }
    }


    @Async
    public void notifySenderAboutChatReceipt(
            Utilisateur sender, String identifiant
    )
    {
        /*Notification builder = new Notification(
                publication.getIdentifiant(),
                "Colis remis au destinataire");*/
        //System.out.println("Sender ID : " + String.valueOf(sender.getId()));
        Message me = Message.builder()
                //.setNotification(builder)
                .setToken(sender.getFcmToken())
                .putData("sujet", "6")  // Subject
                .putData("identifiant", identifiant)
                .build();
        try {
            FirebaseMessaging.getInstance().send(me);
        } catch (FirebaseMessagingException e) {
            System.out.println("notifyOwnerAboutNewReservation : "+e.toString());
        }
    }
}
