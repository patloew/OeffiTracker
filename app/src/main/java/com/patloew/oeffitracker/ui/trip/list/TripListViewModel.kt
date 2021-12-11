package com.patloew.oeffitracker.ui.trip.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.patloew.oeffitracker.data.model.PriceDeduction
import com.patloew.oeffitracker.data.model.Trip
import com.patloew.oeffitracker.data.repository.SettingsRepo
import com.patloew.oeffitracker.data.repository.TicketDao
import com.patloew.oeffitracker.data.repository.TripDao
import com.patloew.oeffitracker.ui.common.ListItem
import com.patloew.oeffitracker.ui.common.ProgressData
import com.patloew.oeffitracker.ui.formatPrice
import com.patloew.oeffitracker.ui.getGoal
import com.patloew.oeffitracker.ui.getSum
import com.patloew.oeffitracker.ui.monthFormat
import com.patloew.oeffitracker.ui.percentageFormat
import com.patloew.oeffitracker.ui.priceFormatFloat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
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

data class TripSection(
    val month: String,
    val fareSum: String
)

class TripListViewModel(
    private val tripDao: TripDao,
    private val ticketDao: TicketDao,
    settingsRepo: SettingsRepo
) : ViewModel() {

    val trips: Flow<PagingData<ListItem<Trip, TripSection>>> =
        Pager(PagingConfig(pageSize = 20)) { tripDao.getAllPagingSource() }.flow
            .map { data ->
                data.map { ListItem.Entry(it) }
                    .insertSeparators { before, after ->
                        if (after?.data != null && before?.data?.date?.month != after.data.date.month) {
                            val startDate = after.data.date.withDayOfMonth(1)
                            val endDate = startDate.plusMonths(1).minusDays(1)
                            val fareSum = tripDao.getSumOfFaresBetween(startDate, endDate) / 100f
                            ListItem.Section(
                                TripSection(
                                    monthFormat.format(after.data.date),
                                    priceFormatFloat.format(fareSum)
                                )
                            )
                        } else {
                            null
                        }
                    }
            }

    val isEmpty: Flow<Boolean> = tripDao.getCount().map { it == 0 }

    val showProgress: Flow<Boolean> = ticketDao.getLatestTicketId().map { it != null }
    private val fareSum: Flow<Int> = tripDao.getSumOfFaresForLatestTicket()
    private val fareSumGoal: Flow<PriceDeduction?> = ticketDao.getLatestTicketPrice()
    val fareProgressData: Flow<ProgressData> =
        combine(
            fareSum,
            fareSumGoal,
            settingsRepo.includeDeductionsInProgress
        ) { sum, priceDeduction, includeDeduction ->
            val priceDeduction = priceDeduction ?: PriceDeduction(price = 0, deduction = null)
            val goal = priceDeduction.getGoal(includeDeduction)
            val progress = priceDeduction.getSum(sum, includeDeduction) / goal.toFloat()
            ProgressData(
                progress = progress.coerceAtMost(1f),
                percentageString = percentageFormat.format(progress),
                priceString = formatPrice(goal)
            )
        }

    private val scrollToTopChannel: Channel<Unit> = Channel(Channel.CONFLATED)
    val scrollToTopEvent: Flow<Unit> = scrollToTopChannel.receiveAsFlow()

    fun onDelete(id: Long) {
        viewModelScope.launch {
            tripDao.deleteById(id)
        }
    }

    fun getTemplateForToday(trip: Trip): Trip = trip.copy(
        id = 0,
        date = LocalDate.now(),
        additionalCosts = null,
        delay = null,
        notes = null,
        createdTimestamp = System.currentTimeMillis()
    )

    fun getReturnTemplate(trip: Trip): Trip = trip.copy(
        id = 0,
        startCity = trip.endCity,
        endCity = trip.startCity,
        date = LocalDate.now(),
        additionalCosts = null,
        delay = null,
        notes = null,
        createdTimestamp = System.currentTimeMillis()
    )
}