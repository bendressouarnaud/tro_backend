package com.ankk.tro.httpbean;

import lombok.Data;

@Data
public class NotificationRequest {
    private long iduser, startdatetime, enddatetime;
    private int choix;
}
