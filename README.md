# Currency Conversion API

A small Spring Boot REST API that converts a monetary amount from one currency to another, using a provided EUR-based exchange rate file. Built as a take-home exercise for the Billing & Payments Tribe.

## Requirements

- JDK 25

## Run

```bash
./gradlew bootRun
```

On Windows: `gradlew.bat bootRun`. The API listens on `http://localhost:8080`.

## Example

```bash
curl -X POST http://localhost:8080/api/v1/conversions \
  -H "Content-Type: application/json" \
  -d '{"sourceCurrency":"EUR","targetCurrency":"USD","amount":100}'
```

```json
{"convertedAmount":116.06}
```

Supported currencies: EUR plus every currency in [`exchange-rates.json`](src/main/resources/exchange-rates.json) (AUD, BRL, CAD, CHF, GBP, NOK, PLN, RON, SEK, USD), in any direction.

Invalid input returns `400` with an problem body:

```json
{
  "type": "about:blank",
  "title": "Invalid request",
  "status": 400,
  "detail": "Invalid request content.",
  "instance": "/api/v1/conversions",
  "errors": [{"field": "sourceCurrency", "message": "must match \"[A-Z]{3}\""}]
}
```

## Tests

```bash
./gradlew test
```

## Structure

- `rates`: loads and validates the exchange rate file at startup, behind the `ExchangeRateProvider` interface.
- `conversion`: REST controller, request/response records, `CurrencyConverter`, error handling.

## Key decisions

- **`BigDecimal` only, no `double`.** Rates are parsed straight into `BigDecimal`.
- **Cross rate through EUR:** `amount × rate(target) / rate(source)`, with `rate(EUR) = 1`. One formula covers EUR → X, X → EUR and X → Y. Multiplication comes before division, so the only inexact step is a single division at `DECIMAL128` precision.
- **One rounding, at the end,** to the target currency's minor units, using `HALF_EVEN` (banker's rounding), which avoids upward bias when amounts are aggregated.
- **Fail fast on bad rate data:** the application does not start if the rate file is invalid.
- **Strict input validation:** upper-case ISO codes, positive amounts, and a digit limit that also guards against huge-exponent inputs such as `1e999999999`.
- **Consistent errors:** always `400` with `ProblemDetail`.


## AI usage

I used an AI coding assistant (Claude Code) for following tasks:
- to check if all basics scenarios was covered in tests
- bootstrap tests boilerplate code
- Final code review
- to create this README.md file
- to create conversions.http file for manual tests
