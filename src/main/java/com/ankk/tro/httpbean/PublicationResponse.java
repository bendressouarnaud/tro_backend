package com.ankk.tro.httpbean;

import lombok.Data;

@Data
public class PublicationResponse {
    long idvilledep, idvilledest,id, provider;
    int prix;
    String date, heure;
}
