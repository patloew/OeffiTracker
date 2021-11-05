package com.patloew.oeffitracker.ui.list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.data.model.Trip
import com.patloew.oeffitracker.data.repository.TripDao
import com.patloew.oeffitracker.ui.PreviewTheme
import com.patloew.oeffitracker.ui.create.CreateActivity
import com.patloew.oeffitracker.ui.theme.OeffiTrackerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.security.SecureRandom
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

class ListActivity : ComponentActivity() {

    private val viewModel: ListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OeffiTrackerTheme {
                MainContent(viewModel.trips)
            }
        }
    }
}

@Composable
fun MainContent(trips: Flow<PagingData<Trip>>) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val listState = rememberLazyListState()
    val createTripLauncher = rememberLauncherForActivityResult(contract = CreateActivity.Contract) { created ->
        if (created) coroutineScope.launch { listState.animateScrollToItem(0) }
    }

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { TopAppBar(title = { Text(stringResource(id = R.string.app_name)) }) },
            floatingActionButton = {
                FloatingActionButton(onClick = { createTripLauncher.launch(Unit) }) {
                    Icon(
                        Icons.Filled.Add,
                        stringResource(id = R.string.accessibility_icon_add),
                        tint = MaterialTheme.colors.onSecondary
                    )
                }
            },
            content = { TripList(trips, listState) }
        )
    }
}

@Composable
fun TripList(trips: Flow<PagingData<Trip>>, listState: LazyListState) {
    val lazyTripItems = trips.collectAsLazyPagingItems()
    LazyColumn(state = listState) {
        items(items = lazyTripItems, key = { it.id }) { trip ->
            TripRow(trip)
            Divider()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListPreview() {
    PreviewTheme {
        TripList(
            trips = flowOf(
                PagingData.from(
                    listOf(
                        Trip("Wien", "Linz", 2000, LocalDate.now(), 0),
                        Trip("Linz", "Graz", 1500, LocalDate.now(), 0)
                    )
                )
            ),
            rememberLazyListState()
        )
    }
}

class ListViewModel(
    private val tripDao: TripDao
) : ViewModel() {

    val trips: Flow<PagingData<Trip>> = Pager(PagingConfig(pageSize = 20)) { tripDao.getAllPagingSource() }.flow

    fun addTrips() {
        viewModelScope.launch {
            val random = SecureRandom()
            tripDao.update(Trip("Wien", "Linz", random.nextInt(10000), LocalDate.now(), System.currentTimeMillis()))
            tripDao.update(
                Trip(
                    "Linz",
                    "St. Pölten",
                    random.nextInt(10000),
                    LocalDate.now(),
                    System.currentTimeMillis()
                )
            )
            tripDao.update(
                Trip(
                    "Vöcklabruck",
                    "Bad Hofgastein",
                    random.nextInt(10000),
                    LocalDate.now(),
                    System.currentTimeMillis()
                )
            )
        }
    }

}