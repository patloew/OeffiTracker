package com.patloew.oeffitracker.ui.ticket.list

import com.patloew.oeffitracker.data.model.Ticket
import com.patloew.oeffitracker.data.model.TicketWithStatistics
import com.patloew.oeffitracker.ui.common.ProgressRoundData
import com.patloew.oeffitracker.ui.dateFormat
import java.time.Duration

data class TicketListData(
    val id: Long,
    val name: String,
    val price: String,
    val validityPeriod: String,
    val progressData: ProgressRoundData,
    val additionalCostsSum: String?,
    val durationSum: Duration?,
    val delaySum: Duration?,
    val distanceSum: Float?,
    val co2savedSum: Float?
)

val Ticket.validityPeriod: String get() = dateFormat.format(startDate) + " – " + dateFormat.format(endDate)
val TicketWithStatistics.validityPeriod: String get() = dateFormat.format(startDate) + " – " + dateFormat.format(endDate)