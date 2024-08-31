package com.ankk.tro.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReservationState {
    EN_COURS(0),
    TRAITE(1);

    private final int value;
}
