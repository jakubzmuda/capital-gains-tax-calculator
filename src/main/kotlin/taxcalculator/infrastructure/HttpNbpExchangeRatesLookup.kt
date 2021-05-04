package taxcalculator.infrastructure

import taxcalculator.domain.ExchangeRatesLookup
import taxcalculator.infrastructure.ExternalApiFactory.Companion.buildNbpApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HttpNbpExchangeRatesLookup : ExchangeRatesLookup {

    private val nbpApi: NbpApi = buildNbpApi()

    override fun lookupExchangeRatesRange(currency: String, startDate: LocalDate, endDate: LocalDate): NbpApi.ExchangeRatesResponse {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return nbpApi.lookupExchangeRates(currency, startDate.format(dateFormatter), endDate.format(dateFormatter)).execute().body()!!
    }
}
