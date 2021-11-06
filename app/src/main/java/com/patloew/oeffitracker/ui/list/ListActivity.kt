package com.patloew.oeffitracker.ui.list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.patloew.oeffitracker.data.model.Trip
import com.patloew.oeffitracker.data.repository.TripDao
import com.patloew.oeffitracker.ui.percentageFormat
import com.patloew.oeffitracker.ui.priceFormat
import com.patloew.oeffitracker.ui.theme.OeffiTrackerTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
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

class ListActivity : ComponentActivity() {

    private val viewModel: ListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OeffiTrackerTheme {
                ListScreen(viewModel)
            }
        }
    }
}

class ListViewModel(
    private val tripDao: TripDao
) : ViewModel() {

    val trips: Flow<PagingData<Trip>> = Pager(PagingConfig(pageSize = 20)) { tripDao.getAllPagingSource() }.flow

    private val fareSum: Flow<Int?> = tripDao.getSumOfFaresBetween(
        DateTimeFormatter.ISO_DATE.format(LocalDate.now().minusYears(1)),
        DateTimeFormatter.ISO_DATE.format(LocalDate.now())
    )
    private val fareSumGoal: Int = 109500
    val fareSumProgress: Flow<Float> = fareSum.map { sum ->
        ((sum ?: 0) / fareSumGoal.toFloat()).coerceAtMost(1f)
    }
    val fareSumPercentageString: Flow<String> = fareSum.map { sum ->
        percentageFormat.format((sum ?: 0) / fareSumGoal.toFloat())
    }
    val fareSumGoalString: Flow<String> = flowOf(priceFormat.format(fareSumGoal / 100f))

    private val scrollToTopChannel: Channel<Unit> = Channel(Channel.CONFLATED)
    val scrollToTopEvent: Flow<Unit> = scrollToTopChannel.receiveAsFlow()

    fun onDelete(id: Int) {
        viewModelScope.launch {
            tripDao.deleteById(id)
        }
    }

    fun onDuplicateForToday(trip: Trip) {
        viewModelScope.launch {
            val newTrip = trip.copy(id = 0, date = LocalDate.now(), createdTimestamp = System.currentTimeMillis())
            tripDao.insert(newTrip)
            scrollToTopChannel.send(Unit)
        }
    }

}