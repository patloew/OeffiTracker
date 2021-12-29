package com.patloew.oeffitracker.data.model

import java.time.Duration
import java.time.LocalDate

data class TicketWithStatistics(
    val id: Long,
    val name: String,
    val price: Long,
    val deduction: Long?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdTimestamp: Long,
    val fareSum: Long,
    val additionalCostsSum: Long?,
    val durationSum: Duration?,
    val delaySum: Duration?,
    val distanceSum: Double?
)