package com.stecalbert.taskawin.conversion;

import com.stecalbert.taskawin.rates.ExchangeRateProvider;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;

@Component
public class CurrencyConverter {

    private static final MathContext PRECISION = MathContext.DECIMAL128;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;

    private final ExchangeRateProvider rates;

    public CurrencyConverter(ExchangeRateProvider rates) {
        this.rates = rates;
    }

    public BigDecimal convert(BigDecimal amount, String sourceCurrency, String targetCurrency) {
        BigDecimal sourceRate = rates.rateFor(sourceCurrency);
        BigDecimal targetRate = rates.rateFor(targetCurrency);
        int minorUnits = Currency.getInstance(targetCurrency).getDefaultFractionDigits();

        return amount.multiply(targetRate, PRECISION)
                .divide(sourceRate, PRECISION)
                .setScale(minorUnits, ROUNDING);
    }
}
