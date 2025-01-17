package com.ankk.tro.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SmartphoneType {
    IPHONE(0),
    ANDROID(1);

    private final int value;
}
