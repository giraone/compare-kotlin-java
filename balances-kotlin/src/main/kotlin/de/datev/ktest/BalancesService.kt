package de.datev.ktest

import java.math.BigDecimal

/*
Geht mit Kotlin 1.6 nicht mehr
fun List<DailyBalance>.sumLatestTotalsByIban() = groupBy { it.iban }
    .map { (_,value) -> value.maxBy { it.day } } // maxBy ist weg!!!
    .sumOf { it.total } it.total nicht mehr erlaubt!!!
*/

fun List<DailyBalance>.sumLatestTotalsByIban() = groupBy { it.iban }
    .map { (_,value) -> value.maxByOrNull { it.day } }
    .sumOf { it!!.total }

class BalancesService {
    // Same code, but callable from Java/JMH
    fun sumLatestTotalsByIban(input: List<DailyBalance>): BigDecimal {
        return input.groupBy { it.iban }
            .map { (_, value) -> value.maxByOrNull { it.day } }
            .sumOf { it!!.total }
    }
}