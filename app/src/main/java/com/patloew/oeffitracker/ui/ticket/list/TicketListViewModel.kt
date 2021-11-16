package com.patloew.oeffitracker.ui.ticket.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.patloew.oeffitracker.data.repository.TicketDao
import com.patloew.oeffitracker.ui.common.ProgressRoundData
import com.patloew.oeffitracker.ui.formatPrice
import com.patloew.oeffitracker.ui.percentageFormat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

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

// Rough estimate, as this depends on many factors.
// Based on: https://www.quarks.de/umwelt/klimawandel/co2-rechner-fuer-auto-flugzeug-und-co/
private const val kgCo2PerKm: Float = 0.15f

class TicketListViewModel(
    private val ticketDao: TicketDao
) : ViewModel() {

    val tickets: Flow<PagingData<TicketListData>> =
        Pager(PagingConfig(pageSize = 20)) { ticketDao.getTicketWithStatisticsPagingSource() }.flow.map { pagingData ->
            pagingData.map { ticket ->
                val percentage: Float = ticket.fareSum / ticket.price.toFloat()
                TicketListData(
                    id = ticket.id,
                    name = ticket.name,
                    price = formatPrice(ticket.fareSum) + " / " + formatPrice(ticket.price),
                    validityPeriod = ticket.validityPeriod,
                    progressData = ProgressRoundData(
                        progress = percentage.coerceAtMost(1f),
                        percentageString = percentageFormat.format(percentage)
                    ),
                    durationSum = ticket.durationSum,
                    delaySum = ticket.delaySum,
                    distanceSum = ticket.distanceSum,
                    co2savedSum = ticket.distanceSum?.times(kgCo2PerKm)
                )
            }
        }

    val highlightedTicketId: Flow<Long?> = ticketDao.getLatestTicketId()

    val isEmpty: Flow<Boolean> = ticketDao.getCount().map { it == 0 }

    private val scrollToTopChannel: Channel<Unit> = Channel(Channel.CONFLATED)
    val scrollToTopEvent: Flow<Unit> = scrollToTopChannel.receiveAsFlow()

    fun onDelete(id: Long) {
        viewModelScope.launch {
            ticketDao.deleteById(id)
        }
    }
}