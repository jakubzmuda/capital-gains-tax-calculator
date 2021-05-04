package taxcalculator.domain

import taxcalculator.infrastructure.NbpApi
import java.time.LocalDate

interface ExchangeRatesLookup {

    fun lookupRateForDay(currency: Currency, day: LocalDate): Float {
        if(currency == Currency("PLN")) {
            return 1f
        }

        return lookupExchangeRatesRange(currency.code, day.minusDays(5), day.minusDays(1))
            .rates.maxByOrNull { LocalDate.parse(it.effectiveDate) }!!
            .mid
    }

    fun lookupExchangeRatesRange(
        currency: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): NbpApi.ExchangeRatesResponse

}
