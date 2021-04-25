package taxcalculator.infrastructure

import taxcalculator.domain.ExchangeRatesLookup
import taxcalculator.infrastructure.ExternalApiFactory.Companion.buildNbpApi
import java.time.LocalDate

class HttpNbpExchangeRatesLookup : ExchangeRatesLookup {

    private val nbpApi: NbpApi = buildNbpApi()

    override fun lookupExchangeRates(currency: String, startDate: LocalDate, endDate: LocalDate): ExchangeRatesResponse {
        return nbpApi.lookupExchangeRates("USD", "2020-01-01", "2020-12-31").execute().body()!!
    }
}
