package pl.maciejlotysz.recruitment_task.task.controller

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import pl.maciejlotysz.recruitment_task.task.model.Customer
import pl.maciejlotysz.recruitment_task.task.model.TransactionFee
import pl.maciejlotysz.recruitment_task.task.service.TransactionFeeService
import java.math.BigDecimal
import java.time.LocalDateTime

@RestController
@RequestMapping("/fees")
class TransactionFeeController(
    private val transactionFeeService: TransactionFeeService,
) {

    @GetMapping("/calculate-commission")
    fun getTransactionValueForCustomer(@RequestParam customerId: List<String>): List<TransactionFeeDto> =
        transactionFeeService.calculateCommission(customerId).map { TransactionFeeDto(it) }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    fun on(ex: NumberFormatException): String? = ex.message
}

data class TransactionFeeDto(
    val customer: CustomerDto,
    val numberOfTransactions: Int,
    val transactionsTotalValue: BigDecimal,
    val feeValue: BigDecimal,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    val lastTransactionDate: LocalDateTime,
) {
    constructor(transactionFee: TransactionFee) : this(
        customer = CustomerDto(transactionFee.customer),
        numberOfTransactions = transactionFee.numberOfTransactions,
        transactionsTotalValue = transactionFee.transactionsTotalValue,
        feeValue = transactionFee.feeValue,
        lastTransactionDate = transactionFee.lastTransactionDate,
    )
}

data class CustomerDto(
    val firstName: String,
    val lastName: String,
    val customerId: Long,
) {
    constructor(customer: Customer) : this(
        firstName = customer.firstName,
        lastName = customer.lastName,
        customerId = customer.customerId,
    )
}


