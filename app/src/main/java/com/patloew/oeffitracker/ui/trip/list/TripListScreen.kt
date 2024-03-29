package com.patloew.oeffitracker.ui.trip.list

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.ui.common.PriceProgress
import com.patloew.oeffitracker.ui.main.Screen
import com.patloew.oeffitracker.ui.navigate
import com.patloew.oeffitracker.ui.trip.create.CreateTripActivity
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripListScreen(navController: NavController, viewModel: TripListViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val createTripLauncher =
        rememberLauncherForActivityResult(contract = CreateTripActivity.CreateContract) { created ->
            if (created) coroutineScope.launch { listState.animateScrollToItem(0) }
        }

    Box(modifier = Modifier.fillMaxSize()) {
        val showProgress = viewModel.showProgress.collectAsState(initial = false).value
        val progressHeight = 48.dp
        val listTopPadding = if (showProgress) progressHeight + 16.dp * 2 else 0.dp

        val items = viewModel.trips.collectAsLazyPagingItems()
        LazyTripList(
            items = items,
            viewModel = viewModel,
            coroutineScope = coroutineScope,
            listState = listState,
            contentPadding = PaddingValues(top = listTopPadding, bottom = 64.dp)
        )

        if (showProgress) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .height(progressHeight),
                onClick = { navController.navigate(Screen.Tickets) }
            ) {
                PriceProgress(
                    progressDataFlow = viewModel.fareProgressData,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        FloatingActionButton(
            onClick = { createTripLauncher.launch(Unit) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                Icons.Filled.Add,
                stringResource(id = R.string.accessibility_icon_add),
            )
        }
    }
}