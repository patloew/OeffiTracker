package com.patloew.oeffitracker.ui.ticket.list

import com.patloew.oeffitracker.data.model.Ticket
import com.patloew.oeffitracker.ui.common.ProgressRoundData
import com.patloew.oeffitracker.ui.dateFormat

data class TicketListData(
    val id: Long,
    val name: String,
    val price: String,
    val validityPeriod: String,
    val progressData: ProgressRoundData
)

val Ticket.validityPeriod: String get() = dateFormat.format(startDate) + " – " + dateFormat.format(endDate)