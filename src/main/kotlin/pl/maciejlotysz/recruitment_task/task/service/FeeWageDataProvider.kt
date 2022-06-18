package pl.maciejlotysz.recruitment_task.task.service

import com.opencsv.CSVReaderBuilder
import org.springframework.stereotype.Service
import pl.maciejlotysz.recruitment_task.task.model.FeeWage
import java.io.FileReader
import javax.annotation.PostConstruct

@Service
class FeeWageDataProvider {

    val feeWages: MutableList<FeeWage> = mutableListOf()

    @PostConstruct
    fun initFeeWageData() {
        val csvReader = CSVReaderBuilder(FileReader(FILE_PATH)).build()
        csvReader.readNext()
        var line: Array<String>? = csvReader.readNext()
        while (line != null) {
            val feeThreshold = line[0].replace(',', '.').toBigDecimal()
            val feePercentage = line[1].replace(',', '.').toBigDecimal()

            feeWages.add(FeeWage(feeThreshold, feePercentage))
            line = csvReader.readNext()
        }
    }

    companion object {
        private const val FILE_PATH = "src/main/resources/files/fee_wages.csv"
    }
}
