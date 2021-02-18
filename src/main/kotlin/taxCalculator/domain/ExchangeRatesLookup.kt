package taxCalculator.domain

import taxCalculator.infrastructure.ExchangeRatesResponse
import java.time.LocalDate

interface ExchangeRatesLookup {

    fun lookupExchangeRates(currency: String, startDate: LocalDate, endDate: LocalDate): ExchangeRatesResponse // TODO later change to domain object

}
