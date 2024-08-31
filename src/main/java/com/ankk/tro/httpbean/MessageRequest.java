package com.ankk.tro.httpbean;

import lombok.Data;

@Data
public class MessageRequest {
    String message, messageid;
    long idpub, iduser, idsouscripteur;
}
