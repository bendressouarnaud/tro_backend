package com.ankk.tro.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReservationState {
    EN_COURS(0),
    EFFECTUE(1),
    TRAITE(2),
    RECU(3),
    RESILIE(4),
    ANNULE(5);

    private final int value;
}
