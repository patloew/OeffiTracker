package com.patloew.oeffitracker.ui.ticket.list

import com.patloew.oeffitracker.ui.common.ProgressRoundData

data class TicketListData(
    val id: Int,
    val name: String,
    val price: String,
    val date: String,
    val progressData: ProgressRoundData
)