package com.ankk.tro.httpbean;

import lombok.Data;

@Data
public class SouscriptionBean {
    long idpub, iduser, millisecondes, reserve;
    int statut;
    String channelid;
}
