package com.patloew.oeffitracker.ui.trip.list

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.data.model.Trip
import com.patloew.oeffitracker.ui.PreviewTheme
import com.patloew.oeffitracker.ui.common.LazyList
import com.patloew.oeffitracker.ui.trip.create.CreateTripActivity
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
fun TripListScreen(viewModel: TripListViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val createTripLauncher = rememberLauncherForActivityResult(contract = CreateTripActivity.Contract) { created ->
        if (created) coroutineScope.launch { listState.animateScrollToItem(0) }
    }

    Surface(color = MaterialTheme.colors.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            TripListContent(
                onDelete = viewModel::onDelete,
                onDuplicateForToday = viewModel::onDuplicateForToday,
                viewModel.trips,
                viewModel.isEmpty,
                viewModel.fareSumGoalString,
                viewModel.fareSumPercentageString,
                viewModel.fareSumProgress,
                listState
            )

            FloatingActionButton(
                onClick = { createTripLauncher.launch(Unit) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Filled.Add,
                    stringResource(id = R.string.accessibility_icon_add),
                    tint = MaterialTheme.colors.onSecondary
                )
            }
        }
    }

    LaunchedEffect(viewModel.scrollToTopEvent) {
        viewModel.scrollToTopEvent.collect { listState.animateScrollToItem(0) }
    }
}

@Composable
fun TripListContent(
    onDelete: (id: Int) -> Unit,
    onDuplicateForToday: (Trip) -> Unit,
    trips: Flow<PagingData<Trip>>,
    isEmpty: Flow<Boolean>,
    fareSumGoal: Flow<String>,
    fareSumPercentage: Flow<String>,
    fareProgress: Flow<Float>,
    listState: LazyListState
) {
    Column {
        Surface(elevation = 8.dp) {
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

        LazyList(
            data = trips,
            getKey = { it.id },
            isEmpty = isEmpty,
            emptyTitleRes = R.string.empty_state_trip_title,
            emptyTextRes = R.string.empty_state_trip_text,
            listState = listState
        ) { trip -> TripItem(trip, onDelete, onDuplicateForToday) }
    }

}

@Preview(showBackground = true)
@Composable
fun TripListPreview() {
    PreviewTheme {
        TripListContent(
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
            isEmpty = flowOf(false),
            fareSumGoal = flowOf(""),
            fareSumPercentage = flowOf(""),
            fareProgress = flowOf(0f),
            rememberLazyListState()
        )
    }
}
