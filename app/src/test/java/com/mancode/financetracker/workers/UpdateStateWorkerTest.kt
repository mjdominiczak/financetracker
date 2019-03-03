package com.mancode.financetracker.workers

import com.google.common.truth.Truth.assertThat
import com.mancode.financetracker.database.workers.generateDatesDaily
import org.junit.Test
import org.threeten.bp.LocalDate

class UpdateStateWorkerTest {

    @Test
    fun generateDatesTest() {
        val from = LocalDate.of(2019, 2, 27)
        val to = LocalDate.of(2019, 3, 2)
        val expected = arrayListOf(
                LocalDate.of(2019, 2, 27),
                LocalDate.of(2019, 2, 28),
                LocalDate.of(2019, 3, 1),
                LocalDate.of(2019, 3, 2)
        )
        assertThat(generateDatesDaily(from, to)).containsExactlyElementsIn(expected)
    }

}