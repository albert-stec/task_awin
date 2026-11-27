package com.stecalbert.taskawin.rates;

import java.math.BigDecimal;

@FunctionalInterface
public interface ExchangeRateProvider {

    BigDecimal rateFor(String currencyCode);
}
