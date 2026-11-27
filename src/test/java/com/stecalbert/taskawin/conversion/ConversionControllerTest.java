package com.stecalbert.taskawin.conversion;

import com.stecalbert.taskawin.rates.UnsupportedCurrencyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversionController.class)
class ConversionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CurrencyConverter converter;

    @Test
    void missingFieldsAreListed() throws Exception {
        convert("{}")
                .andExpect(jsonPath("$.errors[*].field")
                        .value(containsInAnyOrder("sourceCurrency", "targetCurrency", "amount")));
    }

    @Test
    void lowercaseCurrencyIsRejected() throws Exception {
        convert("""
                {"sourceCurrency":"eur","targetCurrency":"USD","amount":100}""")
                .andExpect(jsonPath("$.errors[*].field").value(contains("sourceCurrency")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-5"})
    void nonPositiveAmountIsRejected(String amount) throws Exception {
        convert("""
                {"sourceCurrency":"EUR","targetCurrency":"USD","amount":%s}""".formatted(amount))
                .andExpect(jsonPath("$.errors[*].field").value(contains("amount")));
    }

    @Test
    void hugeExponentAmountIsRejected() throws Exception {
        convert("""
                {"sourceCurrency":"EUR","targetCurrency":"USD","amount":1e999999999}""");

        verifyNoInteractions(converter);
    }

    @Test
    void malformedJsonIsRejected() throws Exception {
        convert("{");
    }

    @Test
    void unsupportedCurrencyIsRejected() throws Exception {
        when(converter.convert(any(BigDecimal.class), anyString(), anyString()))
                .thenThrow(new UnsupportedCurrencyException("JPY"));

        convert("""
                {"sourceCurrency":"JPY","targetCurrency":"USD","amount":100}""")
                .andExpect(jsonPath("$.detail").value("Unsupported currency: JPY"));
    }

    private ResultActions convert(String json) throws Exception {
        return mvc.perform(post("/api/v1/conversions").contentType(APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }
}
