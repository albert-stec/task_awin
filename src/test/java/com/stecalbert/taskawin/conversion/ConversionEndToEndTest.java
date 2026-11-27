package com.stecalbert.taskawin.conversion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ConversionEndToEndTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void convertsTaskExample() throws Exception {
        mvc.perform(post("/api/v1/conversions")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"sourceCurrency":"EUR","targetCurrency":"USD","amount":100}"""))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().string("{\"convertedAmount\":116.06}"));
    }

    @Test
    void sameCurrencyKeepsTwoDecimals() throws Exception {
        mvc.perform(post("/api/v1/conversions")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"sourceCurrency":"USD","targetCurrency":"USD","amount":100}"""))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"convertedAmount\":100.00}"));
    }
}
