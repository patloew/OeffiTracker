package com.patloew.oeffitracker.ui.trip.list.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.ui.common.NavigationBackIcon
import com.patloew.oeffitracker.ui.onPrimarySurface
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

@Composable
fun TripSearchScreen(viewModel: TripSearchViewModel, onNavigationClick: () -> Unit) {
    val scaffoldState = rememberScaffoldState()
    val items = viewModel.trips.collectAsLazyPagingItems()

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = {
                        val queryFocusRequester = remember { FocusRequester() }

                        BasicTextField(
                            modifier = Modifier
                                .fillMaxSize()
                                .focusRequester(queryFocusRequester),
                            textStyle = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.onPrimarySurface),
                            cursorBrush = SolidColor(MaterialTheme.colors.onPrimarySurface),
                            singleLine = true,
                            value = viewModel.query.collectAsState().value,
                            onValueChange = {
                                viewModel.query.value = it
                                items.refresh()
                            },
                            decorationBox = { innerTextField ->
                                Row(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(top = 8.dp, end = 4.dp, bottom = 8.dp)
                                        .background(
                                            MaterialTheme.colors.onPrimarySurface.copy(alpha = 0.2f),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    innerTextField()
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
            content = {
                val coroutineScope = rememberCoroutineScope()
                val listState = rememberLazyListState()

                Box(modifier = Modifier.fillMaxSize()) {
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
}
