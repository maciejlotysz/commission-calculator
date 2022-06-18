package pl.maciejlotysz.recruitment_task.task.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionFee(
    val customer: Customer,
    val numberOfTransactions: Int,
    val transactionsTotalValue: BigDecimal,
    val feeValue: BigDecimal,
    val lastTransactionDate: LocalDateTime,
)

data class Customer(
    val firstName: String,
    val lastName: String,
    val customerId: Long,
)

data class Transaction(
    val transactionId: Long,
    val transactionAmount: BigDecimal,
    val transactionDate: LocalDateTime,
)

data class BankTransfer(
    val customer: Customer,
    val transaction: Transaction,
)
