package com.patloew.oeffitracker.ui.trip.list.search

import androidx.paging.PagingSource
import com.patloew.oeffitracker.data.model.Trip
import com.patloew.oeffitracker.data.repository.TripDao
import com.patloew.oeffitracker.ui.trip.list.BaseTripListViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
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

class TripSearchViewModel(private val tripDao: TripDao) : BaseTripListViewModel(tripDao) {

    val query: MutableStateFlow<String> = MutableStateFlow("")

    private val searchQuery: String
        get() = query.value.trim()

    override val isEmpty: Flow<Boolean> = query.flatMapLatest { query -> tripDao.getSearchCount(query) }
        .map { it == 0 }

    override fun getPagingSource(): PagingSource<Int, Trip> = tripDao.getSearchPagingSource(searchQuery)

    override suspend fun getSumOfFaresBetween(startDate: LocalDate, endDate: LocalDate): Long =
        tripDao.getSearchSumOfFaresBetween(searchQuery, startDate, endDate)
}