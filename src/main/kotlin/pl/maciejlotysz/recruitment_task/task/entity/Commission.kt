package pl.maciejlotysz.recruitment_task.task.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.LocalDateTime

@Document("commissions")
data class Commission(
        @Id
        val id: String? = null,
        val customerId: Long,
        val commissionAmount: BigDecimal,
        val calculationDate: LocalDateTime,
)
