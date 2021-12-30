package com.patloew.oeffitracker.ui.trip.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.data.model.Trip
import com.patloew.oeffitracker.ui.common.LazyList
import com.patloew.oeffitracker.ui.common.ListItem
import com.patloew.oeffitracker.ui.common.SectionHeader
import kotlinx.coroutines.CoroutineScope
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

@Composable
fun LazyTripList(
    items: LazyPagingItems<ListItem<Trip, TripSection>>,
    emptyTitleRes: Int = R.string.empty_state_trip_title,
    emptyTextRes: Int = R.string.empty_state_trip_text,
    viewModel: BaseTripListViewModel,
    coroutineScope: CoroutineScope,
    listState: LazyListState,
    contentPadding: PaddingValues
) {
    LazyList(
        items = items,
        getKey = { listItem ->
            when (listItem) {
                is ListItem.Entry -> listItem.data.id
                is ListItem.Section -> listItem.data.hashCode().shl(16)
            }
        },
        isEmpty = viewModel.isEmpty,
        emptyTitleRes = emptyTitleRes,
        emptyTextRes = emptyTextRes,
        listState = listState,
        contentPadding = contentPadding
    ) { listItem ->
        when (listItem) {
            is ListItem.Entry -> TripItem(
                listItem.data,
                viewModel::onDelete,
                viewModel::getTemplateForToday,
                viewModel::getReturnTemplate,
                scrollToTop = { coroutineScope.launch { listState.animateScrollToItem(0) } }
            )
            is ListItem.Section -> SectionHeader(
                Modifier.padding(horizontal = 16.dp),
                listItem.data.month,
                listItem.data.fareSum
            )
        }
    }
}