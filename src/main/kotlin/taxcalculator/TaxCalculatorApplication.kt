package taxcalculator

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import taxcalculator.domain.*
import taxcalculator.infrastructure.HttpNbpExchangeRatesLookup
import java.io.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

fun main() {
    TaxCalculatorApplication().processFile(
        "src/main/resources/input/Transactions.csv",
        "src/main/resources/output/TransactionReport.csv"
    )
}

class TaxCalculatorApplication {

    private val taxCalculator = TaxCalculator(HttpNbpExchangeRatesLookup())

    fun processFile(inputFilePath: String, outputFilePath: String) {
        val outputFile = prepareOutputFile(outputFilePath)
        produceTransactionReport(FileInputStream(inputFilePath), FileOutputStream(outputFile, false))
    }

    private fun produceTransactionReport(inputStream: InputStream, outputStream: OutputStream) {
        val csv = readCsvFromStream(inputStream)
        writeReportsToStream(parse(csv), outputStream)
    }

    private fun parse(csv: String): List<ClosedPositionReport> {
        val transactions: List<Transaction> = csvReader()
            .readAllWithHeader(csv.trimIndent())
            .map { row ->
                val date = LocalDate.parse(row["Data"], DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                val time = LocalTime.parse(row["Czas"])
                val asset = row["Produkt"]!!
                val isin = row["ISIN"]!!
                val numberOfShares = row["Liczba"]!!.toInt()
                val priceForUnit = Money(
                    row["Kurs"]!!.toFloat().absoluteValue,
                    Currency(row["Waluta zlecenia"]!!)
                )
                val totalPrice = Money(
                    row["Wartość lokalna"]!!.toFloat(),
                    Currency(row["Waluta zlecenia"]!!)
                )
                val commissionAmount = row["Opłata transakcyjna"]
                val commissionCurrency = row["Opłata w walucie"]!!
                val commission = Money(
                    if (!commissionAmount.isNullOrEmpty()) commissionAmount.toFloat() else 0f,
                    Currency(if (commissionCurrency.isNotEmpty()) commissionCurrency else "EUR")
                )
                val stockExchange = StockExchange(row["Giełda referenc"]!!)
                Transaction(
                    date.atTime(time).toInstant(ZoneOffset.UTC),
                    asset,
                    isin,
                    numberOfShares,
                    priceForUnit,
                    totalPrice,
                    commission,
                    stockExchange
                )
            }
            .toList()


        return taxCalculator.calculateTransactionTax(transactions)
    }

    private fun readCsvFromStream(inputStream: InputStream): String {
        return inputStream.bufferedReader().use { it.readText() }
    }

    private fun writeReportsToStream(reports: List<ClosedPositionReport>, outputStream: OutputStream) {
        val header = listOf("Data", "Aktywo", "Liczba akcji / udziałów", "Zysk / Strata [PLN]", "Kraj")
        val rows = reports.map { report -> reportPropertiesAsList(report) }
        csvWriter().writeAll(listOf(header) + rows, outputStream)
    }

    private fun reportPropertiesAsList(report: ClosedPositionReport): List<String> {
        return listOf(
            report.date.toString(),
            report.assetName,
            report.numberOfShares.absoluteValue.toString(),
            BigDecimal(report.gainInPolishCurrency.amount.toString()).setScale(2, RoundingMode.HALF_UP).toString(),
            report.country.code
        )
    }

    private fun prepareOutputFile(outputFilePath: String): File {
        val file = File(outputFilePath)
        file.parentFile.mkdirs()
        file.createNewFile()
        return file
    }
}
