package com.stecalbert.taskawin.conversion;

import java.math.BigDecimal;

public record ConversionRequest(String sourceCurrency, String targetCurrency, BigDecimal amount) {
}
