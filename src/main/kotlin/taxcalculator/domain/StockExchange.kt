package taxcalculator.domain

import java.lang.RuntimeException

class StockExchange(
    val code: String
) {

    fun country(): Country {
        return when(code) {
            "NSY" -> Country("USA")
            "NDQ" -> Country("USA")
            "WSE" -> Country("POL")
            "EAM" -> Country("NL")
            "SGX" -> Country("SG")
            "XET" -> Country("GER")
            "MIL" -> Country("IT")
            else -> throw RuntimeException("Unknown exchange")
        }
    }

}
