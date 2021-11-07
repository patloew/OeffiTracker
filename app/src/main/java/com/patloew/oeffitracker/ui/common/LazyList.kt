package com.patloew.oeffitracker.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import kotlinx.coroutines.flow.Flow

@Composable
fun <T : Any> LazyList(
    data: Flow<PagingData<T>>,
    getKey: (T) -> Any,
    isEmpty: Flow<Boolean>,
    @StringRes emptyTitleRes: Int,
    @StringRes emptyTextRes: Int,
    listState: LazyListState,
    item: @Composable (T?) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (isEmpty.collectAsState(initial = true).value) {
            EmptyState(titleRes = emptyTitleRes, textRes = emptyTextRes)
        } else {
            val lazyTicketItems = data.collectAsLazyPagingItems()
            LazyColumn(state = listState) {
                items(items = lazyTicketItems, key = getKey) { itemData ->
                    item(itemData)
                    Divider()
                }
            }
        }
    }
}