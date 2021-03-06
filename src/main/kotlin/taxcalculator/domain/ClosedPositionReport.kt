package taxcalculator.domain

import java.time.LocalDate

data class ClosedPositionReport(
    val date: LocalDate,
    val assetName: String,
    val numberOfShares: Int,
    val buyPriceInPolishCurrency: Money,
    val sellPriceInPolishCurrency: Money,
    val gainInPolishCurrency: Money,
    val country: Country
)
