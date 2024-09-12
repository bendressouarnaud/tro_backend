package com.ankk.tro.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReservationState {
    EN_COURS(0),
    EFFECTUE(1),
    TRAITE(2);

    private final int value;
}
