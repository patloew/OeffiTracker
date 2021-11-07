package com.patloew.oeffitracker.ui.ticket.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.patloew.oeffitracker.data.model.Ticket
import com.patloew.oeffitracker.data.repository.TicketDao
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

class TicketListViewModel(
    private val ticketDao: TicketDao
) : ViewModel() {

    val tickets: Flow<PagingData<Ticket>> = Pager(PagingConfig(pageSize = 20)) { ticketDao.getAllPagingSource() }.flow

    val isEmpty: Flow<Boolean> = ticketDao.getCount().map { it == 0 }

    private val scrollToTopChannel: Channel<Unit> = Channel(Channel.CONFLATED)
    val scrollToTopEvent: Flow<Unit> = scrollToTopChannel.receiveAsFlow()

    fun onDelete(id: Int) {
        viewModelScope.launch {
            ticketDao.deleteById(id)
        }
    }

}