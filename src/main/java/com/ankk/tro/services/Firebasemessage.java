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

@Service
public class Firebasemessage {

    @Async
    public void notifySuscriberAboutCible(List<String> tokens, Publication publication){
        Notification builder = new Notification(publication.getIdentifiant(),
                ("Réserve initiale : "+ String.valueOf(publication.getReserve()) + " Kg"));
        List<Message> listeMessage = new ArrayList<>();
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
            listeMessage.add(me);
        }
        try {
            FirebaseMessaging.getInstance().sendAll(listeMessage);
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
        Message me = Message.builder()
                //.setNotification(builder)
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
        Notification builder = new Notification((sender.getNom()+" "+sender.getPrenom()),
                "pomme");
        Message me = Message.builder()
                .setNotification(builder)
                .setToken(receiver.getFcmToken())
                .putData("sujet", "3")  // Subject
                .putData("message", chat.getMessage())  // Feed 'Magasin' table :
                .putData("time",
                        String.valueOf(chat.getCreationDatetime().toEpochSecond()*1000))  // Feed 'Magasin' table :
                .putData("idpub", String.valueOf(chat.getPublication().getId()))
                .putData("iduser", String.valueOf(receiver.getId()))
                .putData("sender", String.valueOf(sender.getId()))
                .putData("identifiant", String.valueOf(chat.getIdentifiant()))
                .build();
        try {
            FirebaseMessaging.getInstance().send(me);
            System.out.println("Notif envoyé");
        } catch (FirebaseMessagingException e) {
            System.out.println("notifyOwnerAboutNewChat : "+e.toString());
        }
    }

}
