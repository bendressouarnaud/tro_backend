package com.ankk.tro.httpbean;

import lombok.Data;

@Data
public class ReservationRequest {
    long idpub,iduser;
    int montant, reserve;
}
