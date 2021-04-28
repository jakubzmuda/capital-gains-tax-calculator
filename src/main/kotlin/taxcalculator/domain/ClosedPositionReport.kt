package taxcalculator.domain

import java.time.LocalDate

data class ClosedPositionReport(
    val date: LocalDate,
    val assetName: String,
    val numberOfShares: Int,
    val localGain: Money,
    val gainInPolishCurrency: Money,
    val country: Country
)
