package com.patloew.oeffitracker.ui.trip.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.insertSeparators
import androidx.paging.map
import com.patloew.oeffitracker.data.model.Trip
import com.patloew.oeffitracker.data.repository.TripDao
import com.patloew.oeffitracker.ui.common.ListItem
import com.patloew.oeffitracker.ui.monthFormat
import com.patloew.oeffitracker.ui.priceFormatFloat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

abstract class BaseTripListViewModel(private val tripDao: TripDao) : ViewModel() {

    val trips: Flow<PagingData<ListItem<Trip, TripSection>>> =
        Pager(PagingConfig(pageSize = 20), pagingSourceFactory = ::getPagingSource).flow
            .map { data ->
                data.map { ListItem.Entry(it) }
                    .insertSeparators { before, after ->
                        if (after?.data != null && before?.data?.date?.month != after.data.date.month) {
                            val startDate = after.data.date.withDayOfMonth(1)
                            val endDate = startDate.plusMonths(1).minusDays(1)
                            val fareSum = getSumOfFaresBetween(startDate, endDate) / 100f
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

    abstract val isEmpty: Flow<Boolean>

    abstract fun getPagingSource(): PagingSource<Int, Trip>

    abstract suspend fun getSumOfFaresBetween(startDate: LocalDate, endDate: LocalDate): Long

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