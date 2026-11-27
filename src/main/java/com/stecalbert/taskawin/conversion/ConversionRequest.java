package com.stecalbert.taskawin.conversion;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ConversionRequest(
        @NotNull @Pattern(regexp = "[A-Z]{3}") String sourceCurrency,
        @NotNull @Pattern(regexp = "[A-Z]{3}") String targetCurrency,
        @NotNull @Positive @Digits(integer = 15, fraction = 6) BigDecimal amount) {
}
