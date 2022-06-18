package pl.maciejlotysz.recruitment_task.task.model

import java.math.BigDecimal

data class FeeWage(
    val feeThreshold: BigDecimal,
    val feePercentage: BigDecimal,
)
