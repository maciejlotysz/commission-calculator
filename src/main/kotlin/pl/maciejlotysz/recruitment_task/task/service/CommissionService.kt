package pl.maciejlotysz.recruitment_task.task.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pl.maciejlotysz.recruitment_task.task.entity.Commission
import pl.maciejlotysz.recruitment_task.task.model.TransactionFee
import pl.maciejlotysz.recruitment_task.task.repository.CommissionRepository
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Service
class CommissionService(
    private val transactionsDataProvider: TransactionsDataProvider,
    private val commissionRepository: CommissionRepository,
    private val feeWageDataProvider: FeeWageDataProvider,
) {

    fun calculateCommission(ids: List<String>): MutableList<TransactionFee> {
        try {
            val calculatedCommissions: MutableList<TransactionFee> = mutableListOf()
            val customersIds = transactionsDataProvider.transactions
                    .map { a -> a.customer.customerId }
                    .toSet()

            val isCustomerExists = if (ids.size == 1 && ids[0] == "ALL") false else customersIds.any { it in ids.map { s -> s.toLong() } }

            if (!isCustomerExists) {
                for (customerId in customersIds) {
                    val transactionFee = getCommissionForCustomer(customerId)
                    saveCalculatedCommission(transactionFee)
                    calculatedCommissions.add(transactionFee)
                }
            } else {
                for (customerId in ids.map { it.toLong() }) {
                    if (customersIds.contains(customerId)) {
                        val transactionFee = getCommissionForCustomer(customerId)
                        saveCalculatedCommission(transactionFee)
                        calculatedCommissions.add(transactionFee)
                    } else {
                        continue
                    }
                }
            }
            return calculatedCommissions
        }
        catch (ex: NumberFormatException) {
            logger.info(errorMsg)
            throw NumberFormatException(errorMsg)
        }
    }

    private fun getTransactionsTotalValue(customerId: Long): BigDecimal {
        val transfersValues = transactionsDataProvider.transactions
                .filter { a -> a.customer.customerId == customerId }
                .map { a -> a.transaction.transactionAmount }

        var totalValue = BigDecimal.ZERO
        for (value in transfersValues) {
            totalValue += value
        }
        return totalValue
    }

    private fun getFeeWageForTransactionsValue(transactionsValue: BigDecimal): BigDecimal {
        val maxFeeThreshold = feeWageDataProvider.feeWages
                .maxOfOrNull { a -> a.feeThreshold } ?: BigDecimal.ZERO

        return if (transactionsValue > maxFeeThreshold) BigDecimal.ZERO
                else feeWageDataProvider.feeWages
                                        .sortedBy { it.feeThreshold }
                                        .filter { a -> transactionsValue < a.feeThreshold }
                                        .map { a -> a.feePercentage }
                                        .first()
    }

    private fun saveCalculatedCommission(transactionFee: TransactionFee) {
        Commission(
            customerId = transactionFee.customer.customerId,
            commissionAmount = transactionFee.feeValue,
            calculationDate = LocalDateTime.now()
        ).run { commissionRepository.save(this) }
    }

    private fun getCommissionForCustomer(customerId: Long): TransactionFee {
        val transactionsValue = getTransactionsTotalValue(customerId)
        val feeWage = getFeeWageForTransactionsValue(transactionsValue)
        val feeValue = transactionsValue.times(feeWage).div(ONE_HUNDRED).setScale(2, RoundingMode.HALF_EVEN)

        val numberOfTransactions = transactionsDataProvider.transactions
                .filter { a -> a.customer.customerId == customerId }
                .map { a -> a.transaction.transactionAmount }
                .count()

        val lastTransactionDate = transactionsDataProvider.transactions
                .filter { a -> a.customer.customerId == customerId }
                .map { a -> a.transaction.transactionDate }
                .sorted().reversed()
                .first()

        val customer = transactionsDataProvider.transactions
                .filter { a -> a.customer.customerId == customerId }
                .map { a -> a.customer }
                .first()

        return TransactionFee(customer, numberOfTransactions, transactionsValue, feeValue, lastTransactionDate)
    }

    companion object {
        val ONE_HUNDRED = BigDecimal(100)
        private const val  errorMsg = "Wrong input - only numbers or a word ALL (capitalized) are allowed!"
        val logger: Logger = LoggerFactory.getLogger(CommissionService::class.java)
    }
}
