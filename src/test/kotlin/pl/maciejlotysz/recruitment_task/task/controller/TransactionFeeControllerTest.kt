package pl.maciejlotysz.recruitment_task.task.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import pl.maciejlotysz.recruitment_task.task.repository.CommissionRepository
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
internal class TransactionFeeControllerTest {

    @field: Autowired lateinit var mockMvc: MockMvc
    @field: Autowired lateinit var objectMapper: ObjectMapper
    @field: MockkBean lateinit var commissionRepository: CommissionRepository


    @Test
    @DisplayName("Given 1 correct id in parameters, Should return a calculated commission for one customer")
    @WithMockUser
    fun givenCorrectOneId_whenGetRequest_thenReturnCalculationForOneCustomerInResponse() {
        //when
        every { commissionRepository.save(any()) } returnsArgument 0
        val response = mockMvc.get("/fees/calculate-commission?customerId=1").andReturn().response

        //then
        assertEquals(HttpStatus.OK.value(), response.status)

        //and
        val transaction = objectMapper.readValue(response.contentAsString, object: TypeReference<List<TransactionFeeDto>>() {})
        assertEquals(1L, transaction[0].customer.customerId)
        assertEquals("Andrzej", transaction[0].customer.firstName)
        assertEquals(23, transaction[0].numberOfTransactions)
        assertEquals(BigDecimal.valueOf(4567.58), transaction[0].transactionsTotalValue)
        assertEquals(BigDecimal.valueOf(50.24), transaction[0].feeValue)
    }

    @Test
    @DisplayName("Given 2 correct Ids and 1 wrong in parameters, Should return a calculated commission for two customers")
    @WithMockUser
    fun givenParametersListWithThreeIds_whenGetRequest_thenReturnCalculationForTwoCustomerInResponse() {
        //when
        every { commissionRepository.save(any()) } returnsArgument 0
        val response = mockMvc.get("/fees/calculate-commission?customerId=1,3,8").andReturn().response

        //then
        assertEquals(HttpStatus.OK.value(), response.status)

        //and
        val transaction = objectMapper.readValue(response.contentAsString, object: TypeReference<List<TransactionFeeDto>>() {})
        assertEquals(2, transaction.size)
        assertEquals(1L, transaction[0].customer.customerId)
        assertEquals(23, transaction[0].numberOfTransactions)
        assertEquals(BigDecimal.valueOf(4567.58), transaction[0].transactionsTotalValue)
        assertEquals(BigDecimal.valueOf(50.24), transaction[0].feeValue)
        assertEquals(1, transaction[1].numberOfTransactions)
        assertEquals(BigDecimal.valueOf(0.18), transaction[1].feeValue)
    }

    @Test
    @DisplayName("Given word ALL as parameter, Should return a calculated commission for all customers")
    @WithMockUser
    fun givenParametersListWithWordALL_whenGetRequest_thenReturnCalculationForAllCustomerInResponse() {
        //when
        every { commissionRepository.save(any()) } returnsArgument 0
        val response = mockMvc.get("/fees/calculate-commission?customerId=ALL").andReturn().response

        //then
        assertEquals(HttpStatus.OK.value(), response.status)

        //and
        val transaction = objectMapper.readValue(response.contentAsString, object: TypeReference<List<TransactionFeeDto>>() {})
        assertEquals(5, transaction.size)
        assertEquals(1L, transaction[0].customer.customerId)
        assertEquals(16, transaction[1].numberOfTransactions)
        assertEquals(BigDecimal.valueOf(0.18), transaction[2].feeValue)
        assertEquals(BigDecimal.valueOf(51.48), transaction[3].feeValue)
        assertEquals(BigDecimal.valueOf(38.43), transaction[4].feeValue)
    }

    @Test
    @DisplayName("Given wrong ids in parameters, Should return a calculated commission for all customers")
    @WithMockUser
    fun givenParametersListWithWrongIds_whenGetRequest_thenReturnCalculationForAllCustomerInResponse() {
        //when
        every { commissionRepository.save(any()) } returnsArgument 0
        val response = mockMvc.get("/fees/calculate-commission?customerId=8,9").andReturn().response

        //then
        assertEquals(HttpStatus.OK.value(), response.status)

        //and
        val transaction = objectMapper.readValue(response.contentAsString, object: TypeReference<List<TransactionFeeDto>>() {})
        assertEquals(5, transaction.size)
    }

    @Test
    @DisplayName("Given non numeric parameter, Should throw exception")
    @WithMockUser
    fun givenNonNumericParameter_whenGetRequest_thenThrowException() {
        //when
        val response = mockMvc.get("/fees/calculate-commission?customerId=kotlin").andReturn().response

        //then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status)
    }
}
