package com.patloew.oeffitracker.ui.list

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.data.model.Trip
import com.patloew.oeffitracker.ui.PreviewTheme
import com.patloew.oeffitracker.ui.create.CreateActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
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

@Composable
fun ListScreen(viewModel: ListViewModel) {
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
            content = {
                TripList(
                    onDelete = viewModel::onDelete,
                    onDuplicateForToday = viewModel::onDuplicateForToday,
                    viewModel.trips,
                    viewModel.fareSumGoalString,
                    viewModel.fareSumPercentageString,
                    viewModel.fareSumProgress,
                    listState
                )
            }
        )
    }


    LaunchedEffect(viewModel.scrollToTopEvent) {
        viewModel.scrollToTopEvent.collect { listState.animateScrollToItem(0) }
    }
}

@Composable
fun TripList(
    onDelete: (id: Int) -> Unit,
    onDuplicateForToday: (Trip) -> Unit,
    trips: Flow<PagingData<Trip>>,
    fareSumGoal: Flow<String>,
    fareSumPercentage: Flow<String>,
    fareProgress: Flow<Float>,
    listState: LazyListState
) {
    Column {
        Surface(elevation = 4.dp) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = fareProgress.collectAsState(initial = 0f).value,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                )
                Text(
                    text = stringResource(
                        id = R.string.progress_description,
                        fareSumPercentage.collectAsState(initial = "").value,
                        fareSumGoal.collectAsState(initial = "").value,
                    ),
                    style = MaterialTheme.typography.subtitle2
                )
            }
        }

        val lazyTripItems = trips.collectAsLazyPagingItems()
        LazyColumn(state = listState) {
            items(items = lazyTripItems, key = { it.id }) { trip ->
                TripRow(trip, onDelete, onDuplicateForToday)
                Divider()
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun ListPreview() {
    PreviewTheme {
        TripList(
            onDelete = { },
            onDuplicateForToday = { },
            trips = flowOf(
                PagingData.from(
                    listOf(
                        Trip("Wien", "Linz", 2000, LocalDate.now(), 0),
                        Trip("Linz", "Graz", 1500, LocalDate.now(), 0)
                    )
                )
            ),
            fareSumGoal = flowOf(""),
            fareSumPercentage = flowOf(""),
            fareProgress = flowOf(0f),
            rememberLazyListState()
        )
    }
}
