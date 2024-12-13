package com.ankk.tro.httpbean;

import lombok.Data;

@Data
public class PublicationBean {
    private long id, userid,villedepart,villedestination, souscripteur,devise;
    private String datevoyage;
    private String datepublication;
    private int reserve;
    private int active;
    private int reservereelle;
    private int milliseconds;
    private String identifiant;
    private String streamchannelid;

    private int prix;
    private int read;
}
