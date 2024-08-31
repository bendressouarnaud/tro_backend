package com.ankk.tro.httpbean;

import lombok.Data;

@Data
public class TravelRequest {
    String paysdepart, abrevpaysdepart, villedepart, paysdestination, abrevpaysdestination,
            villedestination, date, heure, heuregeneration, deviselib;
    int reserve, prix;
    long id, idpaysdepart, idvilledepart, idpaysdestination, idvilledestination, user, milliseconds,
            deviseid;
}
