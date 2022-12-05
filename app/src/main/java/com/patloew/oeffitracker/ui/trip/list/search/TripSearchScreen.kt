package com.patloew.oeffitracker.ui.trip.list.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.ui.common.NavigationBackIcon
import com.patloew.oeffitracker.ui.trip.list.LazyTripList

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
fun TripSearchScreen(viewModel: TripSearchViewModel, onNavigationClick: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val items = viewModel.trips.collectAsLazyPagingItems()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    val queryFocusRequester = remember { FocusRequester() }

                    BasicTextField(
                        modifier = Modifier.focusRequester(queryFocusRequester),
                        textStyle = MaterialTheme.typography.titleLarge,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        singleLine = true,
                        value = viewModel.query.collectAsState().value,
                        onValueChange = {
                            viewModel.query.value = it
                            items.refresh()
                        },
                        decorationBox = { innerTextField ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 8.dp)
                            ) {
                                Box(Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                                    innerTextField()
                                }
                            }
                        }
                    )

                    DisposableEffect(Unit) {
                        queryFocusRequester.requestFocus()
                        onDispose { }
                    }
                },
                navigationIcon = { NavigationBackIcon { onNavigationClick() } }
            )
        },
        content = { paddingValues ->
            val coroutineScope = rememberCoroutineScope()
            val listState = rememberLazyListState()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyTripList(
                    items = items,
                    emptyTitleRes = R.string.empty_state_trip_search_title,
                    emptyTextRes = R.string.empty_state_trip_search_text,
                    viewModel = viewModel,
                    coroutineScope = coroutineScope,
                    listState = listState,
                    contentPadding = PaddingValues()
                )
            }
        }
    )
}
