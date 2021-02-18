package taxCalculator.infrastructure

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ExternalApiFactory {

    companion object {
        fun buildNbpApi(): NbpApi {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.nbp.pl/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(NbpApi::class.java)
        }
    }
}
