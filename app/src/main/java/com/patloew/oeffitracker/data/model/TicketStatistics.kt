package com.patloew.oeffitracker.data.model

import java.time.Duration
import java.time.LocalDate

data class TicketWithStatistics(
    val id: Long,
    val name: String,
    val price: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdTimestamp: Long,
    val fareSum: Int,
    val additionalCostsSum: Int?,
    val durationSum: Duration?,
    val delaySum: Duration?,
    val distanceSum: Float?
)