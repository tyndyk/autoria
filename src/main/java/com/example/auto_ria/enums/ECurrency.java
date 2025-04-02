package com.example.auto_ria.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ECurrency {
    USD, GBP, JPY, CNY, CHF, RUB, INR, KRW, ILS, EUR,
    AUD, CAD, HKD, SGD, NZD, SEK, NOK, MXN, BRL, ZAR, AED;

    public static List<String> getCurrencySymbols() {
        return Arrays.stream(values())
                     .map(Enum::name)
                     .collect(Collectors.toList());
    }
}
