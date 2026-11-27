package com.stecalbert.taskawin.rates;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class ClasspathExchangeRateProvider implements ExchangeRateProvider {

    private static final String BASE_CURRENCY = "EUR";

    private final Map<String, BigDecimal> rates;

    public ClasspathExchangeRateProvider(@Value("classpath:exchange-rates.json") Resource resource,
                                         ObjectMapper mapper) throws IOException {
        ExchangeRatesFile file;
        try (InputStream in = resource.getInputStream()) {
            file = mapper.readValue(in, ExchangeRatesFile.class);
        }
        validate(file);

        Map<String, BigDecimal> result = new HashMap<>();
        result.put(BASE_CURRENCY, BigDecimal.ONE);
        file.quotes().forEach((key, rate) -> result.put(key.substring(BASE_CURRENCY.length()), rate));
        this.rates = Map.copyOf(result);
    }

    @Override
    public BigDecimal rateFor(String currencyCode) {
        BigDecimal rate = rates.get(currencyCode);
        if (rate == null) {
            throw new UnsupportedCurrencyException(currencyCode);
        }
        return rate;
    }

    private static void validate(ExchangeRatesFile file) {
        if (!Boolean.TRUE.equals(file.success())) {
            throw new IllegalStateException("Exchange rates: 'success' is not true");
        }
        if (!BASE_CURRENCY.equals(file.source())) {
            throw new IllegalStateException("Exchange rates: 'source' must be " + BASE_CURRENCY);
        }
        if (file.quotes() == null || file.quotes().isEmpty()) {
            throw new IllegalStateException("Exchange rates: 'quotes' are missing or empty");
        }
        file.quotes().forEach((key, rate) -> {
            if (!key.startsWith(BASE_CURRENCY)) {
                throw new IllegalStateException("Exchange rates: quote " + key + " does not start with " + BASE_CURRENCY);
            }
            if (rate == null || rate.signum() <= 0) {
                throw new IllegalStateException("Exchange rates: quote " + key + " must be positive");
            }
        });
    }
}
