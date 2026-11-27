package com.stecalbert.taskawin.rates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
record ExchangeRatesFile(Boolean success, String source, Map<String, BigDecimal> quotes) {
}
