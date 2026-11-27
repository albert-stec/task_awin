package com.stecalbert.taskawin.conversion;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/conversions")
public class ConversionController {

    private final CurrencyConverter converter;

    public ConversionController(CurrencyConverter converter) {
        this.converter = converter;
    }

    @PostMapping
    public ConversionResponse convert(@RequestBody ConversionRequest request) {
        return new ConversionResponse(
                converter.convert(request.amount(), request.sourceCurrency(), request.targetCurrency()));
    }
}
