package com.patloew.oeffitracker.data.export.model.v1

import com.patloew.oeffitracker.data.model.Ticket
import com.squareup.moshi.JsonClass
import java.time.LocalDate

/* Copyright 2021 Patrick Löwenstein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */

@JsonClass(generateAdapter = true)
data class TicketV1(
    val name: String,
    val price: Long,
    val deduction: Long?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdTimestamp: Long
) {
    constructor(ticket: Ticket) : this(
        ticket.name,
        ticket.price,
        ticket.deduction,
        ticket.startDate,
        ticket.endDate,
        ticket.createdTimestamp
    )

    fun toTicket(): Ticket = Ticket(name, price, deduction, startDate, endDate, createdTimestamp)
}
