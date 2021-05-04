package taxcalculator.infrastructure

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NbpApi {

    @GET("api/exchangerates/rates/A/{currency}/{startDate}/{endDate}")
    fun lookupExchangeRates(
        @Path("currency") currency: String,
        @Path("startDate") startDate: String,
        @Path("endDate") endDate: String
    ): Call<ExchangeRatesResponse>

    data class ExchangeRatesResponse(
        val table: String,
        val currency: String,
        val code: String,
        val rates: List<Rate>
    )

    data class Rate(
        val no: String,
        val effectiveDate: String,
        val mid: Float
    )
}


