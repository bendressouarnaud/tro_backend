package com.ankk.tro.httpbean;

import lombok.Data;

@Data
public class UserBean {
    private String nationalite, nom, prenom, adresse;
    long iduser;
}
