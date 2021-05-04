package taxcalculator.domain

import java.time.LocalDate

data class Share(
    val assetName: String,
    val tradedAt: Money,
    val transactionDate: LocalDate,
    val commissionPerShare: Money,
)
