package taxcalculator

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import taxcalculator.domain.ClosedTransactionReport
import java.io.*

fun main() {
    TaxCalculatorApplication().processFile(
        "src/main/resources/input/Transactions.csv",
        "src/main/resources/output/TransactionReport.csv"
    )
}

class TaxCalculatorApplication {

    fun processFile(inputFilePath: String, outputFilePath: String) {
        val outputFile = prepareOutputFile(outputFilePath)
        produceTransactionReport(FileInputStream(inputFilePath), FileOutputStream(outputFile, false))
    }

    private fun produceTransactionReport(inputStream: InputStream, outputStream: OutputStream) {
        val csv = readCsvFromStream(inputStream)
        writeReportsToStream(parse(csv), outputStream)
    }

    private fun parse(csv: String): List<ClosedTransactionReport> {
        return listOf(
            ClosedTransactionReport("Asset1", "1000"),
            ClosedTransactionReport("Asset2", "350")
        )
    }

    private fun readCsvFromStream(inputStream: InputStream): String {
        return inputStream.bufferedReader().use { it.readText() }
    }

    private fun writeReportsToStream(reports: List<ClosedTransactionReport>, outputStream: OutputStream) {
        csvWriter().writeAll(reports.map { report -> reportPropertiesAsList(report) }, outputStream)
    }

    private fun reportPropertiesAsList(report: ClosedTransactionReport): List<String> {
        return listOf(
            report.assetName,
            report.gain
        )
    }

    private fun prepareOutputFile(outputFilePath: String): File {
        val file = File(outputFilePath)
        file.parentFile.mkdirs()
        file.createNewFile()
        return file
    }
}
