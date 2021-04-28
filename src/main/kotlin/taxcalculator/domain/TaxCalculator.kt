package taxcalculator.domain

class TaxCalculator {

    fun calculateTransactionTax(transactions: List<Transaction>): List<ClosedPositionReport> {
        val sellTransactions = sellTransactions(transactions)

        val buyTransactionQueuesPerAsset = buyTransactionQueuesPerAsset(transactions)

        sellTransactions.map { sellTransaction ->
//            val assetBuyTransactionsQueue = buyTransactionQueuesPerAsset[sellTransaction.asset]
//            val accordingBuyTransaction = assetBuyTransactionsQueue.poll() // TODO custom queue required
        }


        return emptyList()
    }

    private fun sellTransactions(transactions: List<Transaction>) =
        transactions.filter { transaction -> transaction.direction() == TransactionDirection.SELL }

    private fun buyTransactionsForAsset(transactions: List<Transaction>, asset: String) =
        transactions.filter { transaction ->
            transaction.direction() == TransactionDirection.BUY && transaction.asset == asset
        }

    private fun buyTransactionQueuesPerAsset(transactions: List<Transaction>): AssetSharesBoughtQueue {
        return transactions
            .map { transaction -> transaction.asset }
            .distinct()
            .map { asset -> asset to buyTransactionsForAsset(transactions, asset) }
            .toMap()
            .let { AssetSharesBoughtQueue(it) }
    }
}
