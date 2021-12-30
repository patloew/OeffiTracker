package com.patloew.oeffitracker.ui.trip.list

import androidx.paging.PagingSource
import com.patloew.oeffitracker.data.model.PriceDeduction
import com.patloew.oeffitracker.data.model.Trip
import com.patloew.oeffitracker.data.repository.SettingsRepo
import com.patloew.oeffitracker.data.repository.TicketDao
import com.patloew.oeffitracker.data.repository.TripDao
import com.patloew.oeffitracker.ui.common.ProgressData
import com.patloew.oeffitracker.ui.formatPrice
import com.patloew.oeffitracker.ui.getGoal
import com.patloew.oeffitracker.ui.getSum
import com.patloew.oeffitracker.ui.percentageFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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

class TripListViewModel(
    private val tripDao: TripDao,
    ticketDao: TicketDao,
    settingsRepo: SettingsRepo
) : BaseTripListViewModel(tripDao) {

    val showProgress: Flow<Boolean> = ticketDao.getLatestTicketId().map { it != null }
    private val fareSum: Flow<Long> = tripDao.getSumOfFaresForLatestTicket()
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
                priceString = goal.formatPrice()
            )
        }

    override val isEmpty: Flow<Boolean> = tripDao.getCount().map { it == 0 }

    override fun getPagingSource(): PagingSource<Int, Trip> = tripDao.getAllPagingSource()

    override suspend fun getSumOfFaresBetween(startDate: LocalDate, endDate: LocalDate): Long =
        tripDao.getSumOfFaresBetween(startDate, endDate)
}