package taxcalculator.domain

import java.time.Instant

data class Transaction(
    val dateTime: Instant,
    val asset: String,
    val isin: String,
    val numberOfShares: Int,
    val priceForUnit: Money,
    val totalPrice: Money,
    val commission: Money,
    val stockExchange: StockExchange
) {

    fun direction(): TransactionDirection {
        return if (numberOfShares > 0) TransactionDirection.BUY else TransactionDirection.SELL
    }

}
