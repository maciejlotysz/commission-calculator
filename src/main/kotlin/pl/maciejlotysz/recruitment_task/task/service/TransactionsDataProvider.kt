package pl.maciejlotysz.recruitment_task.task.service

import com.opencsv.CSVReaderBuilder
import org.springframework.stereotype.Service
import pl.maciejlotysz.recruitment_task.task.model.BankTransfer
import pl.maciejlotysz.recruitment_task.task.model.Customer
import pl.maciejlotysz.recruitment_task.task.model.Transaction
import java.io.FileReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct

@Service
class TransactionsDataProvider {

    val transactions: MutableList<BankTransfer> = mutableListOf()

    @PostConstruct
    fun initTransactionData() {
        val csvReader = CSVReaderBuilder(FileReader(FILE_PATH)).build()
        csvReader.readNext()
        var line: Array<String>? = csvReader.readNext()
        while (line != null) {
            val transactionId = line[0].toLong()
            val transactionAmount = line[1].replace(',', '.').toBigDecimal()
            val customerFirstName = line[2]
            val customerId = line[3].toLong()
            val customerLastName = line[4]
            val transactionDate = LocalDateTime.parse(line[5], DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN))

            val customer = Customer(customerFirstName, customerLastName, customerId)
            val transaction = Transaction(transactionId, transactionAmount, transactionDate)
            val bankTransfer = BankTransfer(customer, transaction)
            transactions.add(bankTransfer)
            line = csvReader.readNext()
        }
    }

    companion object {
        private const val FILE_PATH = "src/main/resources/files/transactions.csv"
        private const val DATE_FORMAT_PATTERN = "dd.MM.yyyy HH:mm:ss"
    }
}
