package com.stecalbert.taskawin.rates;

public class UnsupportedCurrencyException extends RuntimeException {

    public UnsupportedCurrencyException(String currencyCode) {
        super("Unsupported currency: " + currencyCode);
    }
}
