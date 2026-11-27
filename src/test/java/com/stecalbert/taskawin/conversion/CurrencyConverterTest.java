package com.stecalbert.taskawin.conversion;

import com.stecalbert.taskawin.rates.ExchangeRateProvider;
import com.stecalbert.taskawin.rates.UnsupportedCurrencyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrencyConverterTest {

    private static final Map<String, BigDecimal> RATES = Map.of(
            "EUR", BigDecimal.ONE,
            "USD", new BigDecimal("1.16063"),
            "GBP", new BigDecimal("0.856017"),
            "PLN", new BigDecimal("4.325559"),
            "SEK", new BigDecimal("11.03937"),
            "NOK", new BigDecimal("10.90808"));

    private final ExchangeRateProvider rates =
            code -> Optional.ofNullable(RATES.get(code))
                    .orElseThrow(() -> new UnsupportedCurrencyException(code));

    private final CurrencyConverter converter = new CurrencyConverter(rates);

    @ParameterizedTest(name = "{0} {1} -> {2} = {3}")
    @CsvSource({
            "100,     EUR, USD, 116.06",
            "116.06,  USD, EUR, 100.00",
            "100,     GBP, PLN, 505.31",
            "1000000, SEK, NOK, 988107.11",
            "100,     USD, USD, 100.00",
            "0.125,   EUR, EUR, 0.12"
    })
    void converts(String amount, String source, String target, String expected) {
        BigDecimal result = converter.convert(new BigDecimal(amount), source, target);

        assertThat(result).isEqualTo(new BigDecimal(expected));
    }

    @Test
    void unsupportedCurrencyThrows() {
        assertThatThrownBy(() -> converter.convert(BigDecimal.ONE, "JPY", "USD"))
                .isInstanceOf(UnsupportedCurrencyException.class);
    }
}
