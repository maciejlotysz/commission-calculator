package pl.maciejlotysz.recruitment_task.task.service

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import pl.maciejlotysz.recruitment_task.task.model.BankTransfer
import pl.maciejlotysz.recruitment_task.task.model.Customer
import pl.maciejlotysz.recruitment_task.task.model.FeeWage
import pl.maciejlotysz.recruitment_task.task.model.Transaction
import pl.maciejlotysz.recruitment_task.task.repository.CommissionRepository
import java.math.BigDecimal
import java.time.LocalDateTime

internal class CommissionServiceTest {

    companion object {
        val ONE_HUNDRED = BigDecimal(100)
    }

    private val transactionsDataProvider: TransactionsDataProvider = mockk()
    private val feeWageDataProvider: FeeWageDataProvider = mockk()
    private val commissionRepository: CommissionRepository = mockk()
    private val commissionService = CommissionService(transactionsDataProvider, commissionRepository,feeWageDataProvider)

    @Test
    @DisplayName("Should calculate 3.5% commission for value lesser than 1000")
    fun givenValueLesserThan1000_whenCalculateCommission_thenGetFirstFeeThresholdPercentage() {
        //given
        every { commissionRepository.save(any()) } returnsArgument 0
        every { transactionsDataProvider.transactions } returns getTransactionDataForTests()
        every { feeWageDataProvider.feeWages } returns getFeeWageDataForTests()
        val input = listOf("1")

        //when
        val calculation = commissionService.calculateCommission(input)
        val feeWage = calculation[0].feeValue.times(ONE_HUNDRED).div(calculation[0].transactionsTotalValue)

        //then
        assertEquals(1, calculation[0].customer.customerId)
        assertEquals(BigDecimal.valueOf(22.91), calculation[0].feeValue)
        assertEquals(BigDecimal.valueOf(3.5), feeWage.setScale(1))
        assertEquals(BigDecimal.valueOf(654.54), calculation[0].transactionsTotalValue)
    }

    @Test
    @DisplayName("Should calculate 2.5% commission for value lesser than 2500")
    fun givenValueLesserThan2500_whenCalculateCommission_thenGetSecondFeeThresholdPercentage() {
        //given
        every { commissionRepository.save(any()) } returnsArgument 0
        every { transactionsDataProvider.transactions } returns getTransactionDataForTests()
        every { feeWageDataProvider.feeWages } returns getFeeWageDataForTests()
        val input = listOf("2")

        //when
        val calculation = commissionService.calculateCommission(input)
        val feeWage = calculation[0].feeValue.times(ONE_HUNDRED).div(calculation[0].transactionsTotalValue)

        //then
        assertEquals(2, calculation[0].customer.customerId)
        assertEquals(BigDecimal.valueOf(41.89), calculation[0].feeValue)
        assertEquals(BigDecimal.valueOf(2.5), feeWage.setScale(1))
        assertEquals(BigDecimal.valueOf(1675.42), calculation[0].transactionsTotalValue)
        assertEquals(LocalDateTime.of(2021, 12, 22,12,17,15), calculation[0].lastTransactionDate)
    }

    @Test
    @DisplayName("Should calculate 1.1% commission for value lesser than 5000")
    fun givenValueLesserThan5000_whenCalculateCommission_thenGetThirdFeeThresholdPercentage() {
        //given
        every { commissionRepository.save(any()) } returnsArgument 0
        every { transactionsDataProvider.transactions } returns getTransactionDataForTests()
        every { feeWageDataProvider.feeWages } returns getFeeWageDataForTests()
        val input = listOf("3")

        //when
        val calculation = commissionService.calculateCommission(input)
        val feeWage = calculation[0].feeValue.times(ONE_HUNDRED).div(calculation[0].transactionsTotalValue)

        //then
        assertEquals(3, calculation[0].customer.customerId)
        assertEquals(BigDecimal.valueOf(51.71), calculation[0].feeValue)
        assertEquals(BigDecimal.valueOf(1.1), feeWage.setScale(1))
        assertEquals(BigDecimal.valueOf(4701.08), calculation[0].transactionsTotalValue)
        assertEquals(LocalDateTime.of(2021, 12, 19,23,17,15), calculation[0].lastTransactionDate)
    }

    @Test
    @DisplayName("Should calculate 0.00 commission for value bigger than 10000")
    fun givenValueBiggerThan10000_whenCalculateCommission_thenReturnCommissionEqualsZero() {
        //given
        every { commissionRepository.save(any()) } returnsArgument 0
        every { transactionsDataProvider.transactions } returns getTransactionDataForTests()
        every { feeWageDataProvider.feeWages } returns getFeeWageDataForTests()
        val input = listOf("4")

        //when
        val calculation = commissionService.calculateCommission(input)
        val feeWage = calculation[0].feeValue.times(ONE_HUNDRED).div(calculation[0].transactionsTotalValue)

        //then
        assertEquals(4, calculation[0].customer.customerId)
        assertEquals(BigDecimal.ZERO.setScale(2), calculation[0].feeValue)
        assertEquals(BigDecimal.ZERO.setScale(2), feeWage.setScale(2))
        assertEquals(BigDecimal.valueOf(19550.50), calculation[0].transactionsTotalValue)
    }

    @Test
    @DisplayName("Should return calculated commission for all customer when input ALL")
    fun givenValueALLAsParameters_whenCalculateCommission_thenReturnCommissionsForAllCustomers() {
        //given
        every { commissionRepository.save(any()) } returnsArgument 0
        every { transactionsDataProvider.transactions } returns getTransactionDataForTests()
        every { feeWageDataProvider.feeWages } returns getFeeWageDataForTests()
        val input = listOf("ALL")

        //when
        val calculation = commissionService.calculateCommission(input)

        //then
        assertEquals(4, calculation.size)
    }

    @Test
    @DisplayName("Should return calculated commission for all customer when input is wrong Id")
    fun givenWrongIdAsParameter_whenCalculateCommission_thenReturnCommissionsForAllCustomers() {
        //given
        every { commissionRepository.save(any()) } returnsArgument 0
        every { transactionsDataProvider.transactions } returns getTransactionDataForTests()
        every { feeWageDataProvider.feeWages } returns getFeeWageDataForTests()
        val input = listOf("8")

        //when
        val calculation = commissionService.calculateCommission(input)

        //then
        assertEquals(4, calculation.size)
    }

    @Test
    @DisplayName("Should throw exception when input non numeric parameter")
    fun givenNonNumericValue_whenCalculateCommission_thenThrowException() {
        //given
        every { transactionsDataProvider.transactions } returns getTransactionDataForTests()
        every { feeWageDataProvider.feeWages } returns getFeeWageDataForTests()
        val input = listOf("kotlin")

        //when
        val ex = assertThrows(NumberFormatException::class.java) {
            commissionService.calculateCommission(input)
        }

        //then
        assertEquals("Wrong input - only numbers or a word ALL (capitalized) are allowed!", ex.message)

    }



    private fun getTransactionDataForTests(): MutableList<BankTransfer> {
        return mutableListOf(
            BankTransfer(
                Customer("Adam", "Adamowski", 1L),
                Transaction(10L, BigDecimal.valueOf(654.54), LocalDateTime.of(2021, 12, 3,12,20,15))),
            BankTransfer(
                Customer("Bartek", "Bartkowiak", 2L),
                Transaction(20L, BigDecimal.valueOf(854.55), LocalDateTime.of(2021, 12, 3,12,20,15))),
            BankTransfer(
                Customer("Bartek", "Bartkowiak", 2L),
                Transaction(21L, BigDecimal.valueOf(79.64), LocalDateTime.of(2021, 12, 15,12,20,15))),
            BankTransfer(
                Customer("Bartek", "Bartkowiak", 2L),
                Transaction(22L, BigDecimal.valueOf(741.23), LocalDateTime.of(2021, 12, 22,12,17,15))),
            BankTransfer(
                Customer("Celina", "Celinowcz", 3L),
                Transaction(30L, BigDecimal.valueOf(2650.88), LocalDateTime.of(2021, 12, 12,13,17,15))),
            BankTransfer(
                Customer("Celina", "Celinowcz", 3L),
                Transaction(31L, BigDecimal.valueOf(2050.20), LocalDateTime.of(2021, 12, 19,23,17,15))),
            BankTransfer(
                Customer("Darek", "Darecki", 4L),
                Transaction(40L, BigDecimal.valueOf(19550.50), LocalDateTime.of(2021, 12, 12,13,17,15)))
        )
    }

    private fun getFeeWageDataForTests(): MutableList<FeeWage> {
        return mutableListOf(FeeWage(BigDecimal.valueOf(1000), BigDecimal.valueOf(3.5)),
                             FeeWage(BigDecimal.valueOf(2500), BigDecimal.valueOf(2.5)),
                             FeeWage(BigDecimal.valueOf(5000), BigDecimal.valueOf(1.1)),
                             FeeWage(BigDecimal.valueOf(10000), BigDecimal.valueOf(0.1))
        )

    }


}
