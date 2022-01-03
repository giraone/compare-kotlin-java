package de.datev.ktest

import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class BalancesServiceKtTest {

    @Test
    fun sumLatestTotalsByIban() {

        val input = listOf(
            DailyBalance("DE123", LocalDate.of(2021, 5, 2), BigDecimal("500.12")),
            DailyBalance("DE123", LocalDate.of(2021, 5, 3), BigDecimal("1234.12")),
            DailyBalance("DE987", LocalDate.of(2021, 5, 1), BigDecimal("145.23")),
            DailyBalance("DE987", LocalDate.of(2021, 5, 5), BigDecimal("600.88"))
        )
        val output = input.sumLatestTotalsByIban()
        assertEquals(BigDecimal("1835.00"), output)
    }

    @Test
    fun sumLatestTotalsByIbanCallableFromOutside() {

        val input = listOf(
            DailyBalance("DE123", LocalDate.of(2021, 5, 2), BigDecimal("500.12")),
            DailyBalance("DE123", LocalDate.of(2021, 5, 3), BigDecimal("1234.12")),
            DailyBalance("DE987", LocalDate.of(2021, 5, 1), BigDecimal("145.23")),
            DailyBalance("DE987", LocalDate.of(2021, 5, 5), BigDecimal("600.88"))
        )
        val output = BalancesService().sumLatestTotalsByIban(input)
        assertEquals(BigDecimal("1835.00"), output)
    }
}