package com.ankk.tro.httpbean;

import lombok.Data;

@Data
public class CibleRequest {
    long id,iduser,idpaysdep,idvilledep,idpaysdest,idvilledest;
    String paysdeplib,paysdepabrev,villedeplib,paysdestlib,paysdestabrev,villedestlib,topic;
}
