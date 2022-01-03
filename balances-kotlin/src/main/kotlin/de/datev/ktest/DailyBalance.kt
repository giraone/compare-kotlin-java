package de.datev.ktest

import java.math.BigDecimal
import java.time.LocalDate

data class DailyBalance(
    val iban: String,
    val day: LocalDate,
    val total: BigDecimal
)
