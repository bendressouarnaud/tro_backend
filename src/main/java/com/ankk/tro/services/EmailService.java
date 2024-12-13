package com.ankk.tro.services;

import com.ankk.tro.model.Utilisateur;
import io.getstream.chat.java.models.Channel;
import io.getstream.chat.java.models.User;
import io.getstream.chat.java.services.framework.Client;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    // A t t r i b u t e s :
    //@Autowired
    private final JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    private String expediteur;


    // MAIL :
    @Async
    public void mailCreation(String objet, String identifiant, String motpasse, int... args){
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true,
                    "utf-8");
            StringBuilder contenu = new StringBuilder();
            contenu.append("<h2> Informations relatives au compte </h2>");
            contenu.append("<div><p>Identifiant : <span style='font-weight:bold;'>" + identifiant + "</span></p></div>");
            contenu.append("<div><p>Mot de passe : <span style='font-weight:bold;'>" + motpasse + "</span></p></div>");
            //
            helper.setText(String.valueOf(contenu), true);
            helper.setTo(identifiant);
            helper.setSubject(objet);
            helper.setFrom(expediteur);
            emailSender.send(mimeMessage);
        } catch (Exception exc) {
            //
            //System.out.println("exc : "+exc.toString());
        }
    }

    @Async
    public void notificationRemboursement(String objet, Utilisateur utilisateur,
        String montant, String idPublication){
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true,
                    "utf-8");
            StringBuilder contenu = new StringBuilder();
            contenu.append("<h2> Informations remboursement </h2>");
            contenu.append("<h3> Un remboursement sera effectu&eacute; sur votre compte </h3>");
            contenu.append("<div><p>Montant : <span style='font-weight:bold;'>" + montant + "</span></p></div>");
            contenu.append("<div><p>Num&eacute;ro annonce : <span style='font-weight:bold;'>" + idPublication + "</span></p></div>");
            helper.setText(String.valueOf(contenu), true);
            helper.setTo(utilisateur.getEmail());
            helper.setSubject(objet);
            helper.setBcc("ngbandamakonan@gmail.com");
            helper.setFrom(expediteur);
            emailSender.send(mimeMessage);
        } catch (Exception exc) {
            //
        }
    }

    @Async
    public void notificationLivraison(String emailSouscripteur,
                                          String emetteur, String idPublication){
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true,
                    "utf-8");
            StringBuilder contenu = new StringBuilder();
            contenu.append("<h2> Informations LIVRAISON </h2>");
            contenu.append("<h3> La livraison de votre colis a &eacute;t&eacute; effectu&eacute; par ");
            contenu.append(emetteur);
            contenu.append("</h3>");
            helper.setText(String.valueOf(contenu), true);
            helper.setTo(emailSouscripteur);
            helper.setSubject("Livraison colis "+idPublication);
            helper.setBcc("ngbandamakonan@gmail.com");
            helper.setFrom(expediteur);
            emailSender.send(mimeMessage);
        } catch (Exception exc) {
            //
        }
    }

    @Async
    public void mailNotification(String destinataire,
                                      String sujet, String message){
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true,
                    "utf-8");

            helper.setText(String.valueOf(message), true);
            helper.setTo(destinataire);
            helper.setSubject(sujet);
            helper.setBcc("ngbandamakonan@gmail.com");
            helper.setFrom(expediteur);
            emailSender.send(mimeMessage);
        } catch (Exception exc) {
            //
        }
    }

    @Async
    public void syncUserId(String id, String name) {
        try {
            var usersUpsertRequest = User.upsert();
            usersUpsertRequest.user(User.UserRequestObject.builder().id(id).name(name).build());
            var response = usersUpsertRequest.request();
            System.out.println("response : "+response.toString());

            // Register DEVICE :

        }
        catch (Exception exc) {
            System.out.println("syncUserId EXCEPTION : "+exc.toString());
        }
    }

    //
    @Async
    public void addMembersToChannels(List<Utilisateur> users, String channel) {
        try {
            var gandalf =
                    User.UserRequestObject.builder()
                            .id(users.get(0).getStreamChatId())
                            .name(users.get(0).getNom())
                            .build();
            Channel.getOrCreate("messaging", channel)
                    .data(Channel.ChannelRequestObject.builder()
                        .createdBy(gandalf)
                        .build()
                    )
                    .request();
            for(Utilisateur user : users) {
                Channel.update("messaging", channel).addMember(user.getStreamChatId()).hideHistory(true).request();
            }
        }
        catch (Exception exc) {
            System.out.println("addMembersToChannels EXCEPTION : "+exc.toString());
        }
    }
}
