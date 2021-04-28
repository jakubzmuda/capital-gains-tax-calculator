package taxcalculator

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import taxcalculator.domain.*
import java.io.*
import java.time.*
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

fun main() {
    TaxCalculatorApplication().processFile(
        "src/main/resources/input/Transactions.csv",
        "src/main/resources/output/TransactionReport.csv"
    )
}

class TaxCalculatorApplication {

    private val taxCalculator = TaxCalculator()

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
                Transaction(
                    date.atTime(time).toInstant(ZoneOffset.UTC),
                    asset,
                    isin,
                    numberOfShares,
                    priceForUnit,
                    totalPrice,
                    commission
                )
            }
            .toList()


            taxCalculator.calculateTransactionTax(transactions)

        return emptyList()
    }

    private fun readCsvFromStream(inputStream: InputStream): String {
        return inputStream.bufferedReader().use { it.readText() }
    }

    private fun writeReportsToStream(reports: List<ClosedPositionReport>, outputStream: OutputStream) {
        csvWriter().writeAll(reports.map { report -> reportPropertiesAsList(report) }, outputStream)
    }

    private fun reportPropertiesAsList(report: ClosedPositionReport): List<String> {
        return emptyList()
    }

    private fun prepareOutputFile(outputFilePath: String): File {
        val file = File(outputFilePath)
        file.parentFile.mkdirs()
        file.createNewFile()
        return file
    }
}
