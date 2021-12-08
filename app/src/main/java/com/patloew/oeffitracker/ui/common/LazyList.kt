package com.patloew.oeffitracker.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import kotlinx.coroutines.flow.Flow

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
fun <T : Any> LazyList(
    data: Flow<PagingData<T>>,
    getKey: (T) -> Any,
    isEmpty: Flow<Boolean>,
    @StringRes emptyTitleRes: Int,
    @StringRes emptyTextRes: Int,
    listState: LazyListState,
    contentPadding: PaddingValues = PaddingValues(bottom = 84.dp),
    item: @Composable LazyItemScope.(T?) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (isEmpty.collectAsState(initial = true).value) {
            EmptyState(titleRes = emptyTitleRes, textRes = emptyTextRes)
        } else {
            val lazyTicketItems = data.collectAsLazyPagingItems()
            LazyColumn(state = listState, contentPadding = contentPadding) {
                items(items = lazyTicketItems, key = getKey) { itemData ->
                    item(itemData)
                    Divider()
                }
            }
        }
    }
}