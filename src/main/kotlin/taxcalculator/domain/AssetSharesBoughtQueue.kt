package taxcalculator.domain

class AssetSharesBoughtQueue(private val buyTransactions: Map<String, List<Transaction>>) {

    private var sharesPoppedPerAsset = buyTransactions.mapKeys { key -> key to 0 }

    fun popShares(assetName: String, numberOfShares: Int) {
        val buyTransactions = buyTransactions[assetName]


    }

}
