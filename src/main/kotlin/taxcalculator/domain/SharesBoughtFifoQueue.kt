package taxcalculator.domain

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.ZoneOffset
import java.util.*

class SharesBoughtFifoQueue(transactions: List<Transaction>) {

    private val sharesQueue: Queue<Share> = LinkedList(
        transactions
            .sortedBy { transaction -> transaction.dateTime }
            .flatMap { transaction ->
                listOfNElements(
                    transaction.numberOfShares,
                    Share(
                        transaction.asset,
                        transaction.priceForUnit,
                        transaction.dateTime.atZone(ZoneOffset.UTC).toLocalDate(),
                        Money(
                            BigDecimal(transaction.commission.amount.toString())
                                .divide(BigDecimal(transaction.numberOfShares), 4, RoundingMode.CEILING)
                                .toFloat(), transaction.totalPrice.currency
                        )
                    )
                )
            }
    )

    fun popOldest(numberOfShares: Int): List<Share> {
        val sharesPopped = mutableListOf<Share>()
        repeat(numberOfShares) {
            sharesPopped.add(sharesQueue.poll())
        }
        return sharesPopped
    }

    private fun <T> listOfNElements(numberOfElements: Int, x: T): MutableList<T> {
        return MutableList(numberOfElements) { x }
    }

}
