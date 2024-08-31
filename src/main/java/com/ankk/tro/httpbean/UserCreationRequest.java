package com.ankk.tro.httpbean;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UserCreationRequest {
    String nom, prenom, email, contact, adresse, codeinvitation, numeropieceidentite,
            pays, abreviationpays,typepieceidentite,token,ville;
    long idpays, idville;
}
