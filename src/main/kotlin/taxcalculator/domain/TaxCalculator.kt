package taxcalculator.domain

import java.time.ZoneOffset
import kotlin.math.abs
import kotlin.math.absoluteValue

class TaxCalculator(
    private val exchangeRatesLookup: ExchangeRatesLookup
) {

    fun calculateTransactionTax(transactions: List<Transaction>): List<ClosedPositionReport> {
        val sellTransactions = sellTransactions(transactions)

        val sharesBoughtPerAsset = sharesBoughtPerAsset(transactions)

        return sellTransactions.map { sellTransaction ->
            println("Processing transaction report for ${sellTransaction.asset}")

            val sharesBought = sharesBoughtPerAsset[sellTransaction.asset]!!
            val sharesBeingSold = sharesBought.popOldest(abs(sellTransaction.numberOfShares))

            val anyShareBeingSold = sharesBeingSold.first()
            val localCurrency = anyShareBeingSold.tradedAt.currency

            val sharesSellPriceInPolishCurrency =
                sellTransaction.totalPrice.amount.absoluteValue * exchangeRatesLookup.lookupRateForDay(
                    localCurrency,
                    sellTransaction.dateTime.atZone(ZoneOffset.UTC).toLocalDate(),
                )

            val sharesBuyPriceInLocalCurrency = sharesBeingSold
                .fold(
                    Money(0f, localCurrency),
                    { acc, curr -> acc + curr.tradedAt })

            val sharesBuyPriceInPolishCurrency =
                sharesBuyPriceInLocalCurrency.amount.absoluteValue * exchangeRatesLookup.lookupRateForDay(
                    localCurrency,
                    anyShareBeingSold.transactionDate
                )

            val commissionInLocalCurrency = sharesBeingSold
                .fold(
                    Money(0f, localCurrency),
                    { acc, curr -> acc + curr.commissionPerShare })

            val commissionInPolishCurrency =
                commissionInLocalCurrency.amount.absoluteValue * exchangeRatesLookup.lookupRateForDay(
                    localCurrency,
                    anyShareBeingSold.transactionDate
                )

            val gainInPolishCurrency = Money(
                sharesSellPriceInPolishCurrency - sharesBuyPriceInPolishCurrency - commissionInPolishCurrency,
                Currency("PLN")
            )

            val country = sellTransaction.stockExchange.country()

            ClosedPositionReport(
                sellTransaction.dateTime.atZone(ZoneOffset.UTC).toLocalDate(),
                sellTransaction.asset,
                sellTransaction.numberOfShares,
                gainInPolishCurrency,
                country
            )
        }
    }

    private fun sellTransactions(transactions: List<Transaction>) =
        transactions
            .filter { transaction -> transaction.direction() == TransactionDirection.SELL }

    private fun possessedSharesForAsset(transactions: List<Transaction>, asset: String) =
        transactions
            .filter { transaction ->
                transaction.direction() == TransactionDirection.BUY && transaction.asset == asset
            }
            .let {
                SharesBoughtFifoQueue(it)
            }

    private fun sharesBoughtPerAsset(transactions: List<Transaction>): Map<String, SharesBoughtFifoQueue> {
        return transactions
            .map { transaction -> transaction.asset }
            .distinct()
            .map { asset -> asset to possessedSharesForAsset(transactions, asset) }
            .toMap()
    }
}
