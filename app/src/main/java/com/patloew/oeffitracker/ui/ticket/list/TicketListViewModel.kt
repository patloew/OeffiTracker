package com.patloew.oeffitracker.ui.ticket.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.patloew.oeffitracker.data.repository.SettingsRepo
import com.patloew.oeffitracker.data.repository.TicketDao
import com.patloew.oeffitracker.data.repository.TripDao
import com.patloew.oeffitracker.ui.common.ProgressRoundData
import com.patloew.oeffitracker.ui.dateFormat
import com.patloew.oeffitracker.ui.formatPrice
import com.patloew.oeffitracker.ui.percentageFormat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

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

class TicketListViewModel(
    private val ticketDao: TicketDao,
    private val tripDao: TripDao,
    private val settingsRepo: SettingsRepo
) : ViewModel() {

    val tickets: Flow<PagingData<TicketListData>> =
        Pager(PagingConfig(pageSize = 20)) { ticketDao.getAllPagingSource() }.flow.map { pagingData ->
            pagingData.map { ticket ->
                val priceSum: Int = tripDao.getSumOfFaresBetween(
                    DateTimeFormatter.ISO_DATE.format(ticket.startDate),
                    DateTimeFormatter.ISO_DATE.format(ticket.endDate)
                )
                val percentage: Float = priceSum / ticket.price.toFloat()
                TicketListData(
                    id = ticket.id,
                    name = ticket.name,
                    price = formatPrice(priceSum) + " / " + formatPrice(ticket.price),
                    date = dateFormat.format(ticket.startDate) + " – " + dateFormat.format(ticket.endDate),
                    progressData = ProgressRoundData(
                        progress = percentage.coerceAtMost(1f),
                        percentageString = percentageFormat.format(percentage)
                    )
                )
            }
        }

    val highlightedTicketId: Flow<Long?> = settingsRepo.highlightedTicketIdFlow()

    val isEmpty: Flow<Boolean> = ticketDao.getCount().map { it == 0 }

    private val scrollToTopChannel: Channel<Unit> = Channel(Channel.CONFLATED)
    val scrollToTopEvent: Flow<Unit> = scrollToTopChannel.receiveAsFlow()

    fun onDelete(id: Long) {
        viewModelScope.launch {
            ticketDao.deleteById(id)
            if (settingsRepo.getHighlightedTicketId() == id) {
                settingsRepo.setHighlightedTicketId(null)
            }
        }
    }

    fun onMakeFavorite(id: Long) {
        viewModelScope.launch {
            settingsRepo.setHighlightedTicketId(id)
        }
    }

}