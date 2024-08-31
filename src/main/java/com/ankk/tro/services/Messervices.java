package com.ankk.tro.services;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;

@Service
public class Messervices {


    public String generatePublicationId(String user, long id){
        OffsetDateTime offsetDateTime = OffsetDateTime.now(Clock.systemUTC());
        //String.valueOf(offsetDateTime.getYear()).substring(2,2);
        String[] tamponName = user.split(" ");
        StringBuilder finalName = new StringBuilder();
        finalName.append(String.valueOf(offsetDateTime.getYear()).substring(2,4));
        finalName.append(String.valueOf(id));
        for(String name : tamponName){
            finalName.append(name.charAt(0));
        }
        finalName.append(String.valueOf(offsetDateTime.getMonthValue()));
        finalName.append(String.valueOf(offsetDateTime.getDayOfMonth()));
        finalName.append(String.valueOf(offsetDateTime.getHour()));
        finalName.append(String.valueOf(offsetDateTime.getMinute()));
        finalName.append(String.valueOf(offsetDateTime.getSecond()));
        return finalName.toString();
    }

    // Generate Password
    public String generatePwd(String user){
        OffsetDateTime offsetDateTime = OffsetDateTime.now(Clock.systemUTC());
        String[] tamponName = user.split(" ");
        StringBuilder finalName = new StringBuilder();
        for(String name : tamponName){
            finalName.append(name.charAt(0));
        }
        finalName.append(String.valueOf(offsetDateTime.getMonthValue()));
        finalName.append(String.valueOf(offsetDateTime.getDayOfMonth()));
        return finalName.toString();
    }
}
