package taxCalculator.application

import taxCalculator.infrastructure.HttpNbpExchangeRatesLookup
import java.time.LocalDate

fun main() {
    val lookupExchangeRates = HttpNbpExchangeRatesLookup().lookupExchangeRates("", LocalDate.now(), LocalDate.now())
    println(lookupExchangeRates)
}

class TaxCalculatorApplication {

    fun processFile(path: String) { // TODO maybe broker as well?
    }

}
