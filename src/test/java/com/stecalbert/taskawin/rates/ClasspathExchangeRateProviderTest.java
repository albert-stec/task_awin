package com.stecalbert.taskawin.rates;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.math.BigDecimal;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClasspathExchangeRateProviderTest {

    @Test
    void loadsBundledFileWithFullPrecision() throws IOException {
        var provider = provider(new ClassPathResource("exchange-rates.json"));

        assertThat(provider.rateFor("USD")).isEqualTo(new BigDecimal("1.16063"));
        assertThat(provider.rateFor("PLN")).isEqualTo(new BigDecimal("4.325559"));
    }

    @Test
    void baseCurrencyHasRateOne() throws IOException {
        assertThat(provider(validJson()).rateFor("EUR")).isEqualByComparingTo("1");
    }

    @Test
    void unknownCurrencyThrows() throws IOException {
        var provider = provider(validJson());

        assertThatThrownBy(() -> provider.rateFor("JPY"))
                .isInstanceOf(UnsupportedCurrencyException.class)
                .hasMessage("Unsupported currency: JPY");
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource(delimiter = '|', textBlock = """
            success is false   | {"success":false,"source":"EUR","quotes":{"EURUSD":1.16}} | success
            source is not EUR  | {"success":true,"source":"USD","quotes":{"USDGBP":0.73}}  | source
            quotes are empty   | {"success":true,"source":"EUR","quotes":{}}               | quotes
            key without EUR    | {"success":true,"source":"EUR","quotes":{"USDGBP":0.73}}  | USDGBP
            non-positive rate  | {"success":true,"source":"EUR","quotes":{"EURUSD":0}}     | EURUSD
            """)
    void rejectsInvalidFile(String description, String json, String expectedMessageFragment) {
        assertThatThrownBy(() -> provider(json))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(expectedMessageFragment);
    }

    private static String validJson() {
        return """
                {"success":true,"source":"EUR","quotes":{"EURUSD":1.16063}}""";
    }

    private static ClasspathExchangeRateProvider provider(String json) throws IOException {
        return provider(new ByteArrayResource(json.getBytes(UTF_8)));
    }

    private static ClasspathExchangeRateProvider provider(Resource resource) throws IOException {
        return new ClasspathExchangeRateProvider(resource, JsonMapper.builder().build());
    }
}
