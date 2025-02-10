package com.ankk.tro.services;

import com.ankk.tro.enums.SmartphoneType;
import com.ankk.tro.httpbean.UserTokenMobileOs;
import com.ankk.tro.model.Utilisateur;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
public class Messervices {

    public String generateCodeFiliation(String user, long id){
        String[] tamponNom = user.split(" ");
        OffsetDateTime offsetDateTime = OffsetDateTime.now(Clock.systemUTC());
        return String.valueOf(offsetDateTime.getYear()).substring(2,4) +
                tamponNom[0].charAt(0) + tamponNom[1].charAt(0) + String.valueOf(id);
    }

    public String generatePublicationId(String user, long id,int... valeur){
        OffsetDateTime offsetDateTime = OffsetDateTime.now(Clock.systemUTC());
        String[] tamponName = user.split(" ");
        StringBuilder finalName = new StringBuilder();
        finalName.append(String.valueOf(offsetDateTime.getYear()).substring(2,4));
        finalName.append(String.valueOf(id));
        for(String name : tamponName){
            finalName.append(name.charAt(0));
        }
        if(valeur.length == 0) {
            //finalName.append(String.valueOf(offsetDateTime.getMonthValue()));
            finalName.append(String.valueOf(offsetDateTime.getDayOfMonth()));
            finalName.append(String.valueOf(offsetDateTime.getHour()));
            //finalName.append(String.valueOf(offsetDateTime.getMinute()));
            finalName.append(String.valueOf(offsetDateTime.getSecond()));
        }
        return finalName.toString();
    }

    // Generate Password
    public String generatePwd(String user){
        OffsetDateTime offsetDateTime = OffsetDateTime.now(Clock.systemUTC());
        String[] tamponName = user.split(" ");
        StringBuilder finalName = new StringBuilder();
        String year = String.valueOf(offsetDateTime.getYear());
        finalName.append(year, 2, 4);
        for(String name : tamponName){
            finalName.append(name.charAt(0));
        }
        finalName.append(String.valueOf(offsetDateTime.getMonthValue()));
        finalName.append(String.valueOf(offsetDateTime.getDayOfMonth()));
        return finalName.toString();
    }

    public boolean checkNotificationRestriction(Utilisateur utilisateur, OffsetDateTime creation){
        if(utilisateur.getNotificationsParam().getChoix() == 1){
            OffsetDateTime debut = utilisateur.getNotificationsParam().getDebut();
            OffsetDateTime fin = utilisateur.getNotificationsParam().getFin();
            Duration durationDebut = Duration.between(debut, creation);
            Duration durationFin = Duration.between(fin, creation);
            boolean retour = durationDebut.getSeconds() >= 0 && durationFin.getSeconds() <= 0;
            return retour;
        }
        else{
            return true;
        }
    }

    public String generateCustomUserId(String name, String prenom, Long ids){
        return name.charAt(0) + prenom.substring(0, 2) + ids.toString();
    }

    public UserTokenMobileOs generateObject(Utilisateur utilisateur){
        UserTokenMobileOs uos = new UserTokenMobileOs();
        uos.setToken(utilisateur.getFcmToken());
        uos.setSmartphoneType(utilisateur.getSmartphoneType() == SmartphoneType.IPHONE ? 0 : 1);
        uos.setEmail(utilisateur.getEmail());
        return uos;
    }
}
