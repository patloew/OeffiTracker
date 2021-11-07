package com.patloew.oeffitracker.ui.ticket.list

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.data.model.Ticket
import com.patloew.oeffitracker.ui.PreviewTheme
import com.patloew.oeffitracker.ui.common.LazyList
import com.patloew.oeffitracker.ui.ticket.create.CreateTicketActivity
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
fun TicketListScreen(viewModel: TicketListViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val createTicketLauncher = rememberLauncherForActivityResult(contract = CreateTicketActivity.Contract) { created ->
        if (created) coroutineScope.launch { listState.animateScrollToItem(0) }
    }

    Surface(color = MaterialTheme.colors.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyList(
                data = viewModel.tickets,
                getKey = { it.id },
                isEmpty = viewModel.isEmpty,
                emptyTitleRes = R.string.empty_state_ticket_title,
                emptyTextRes = R.string.empty_state_ticket_text,
                listState = listState
            ) { ticket -> TicketItem(ticket) }

            FloatingActionButton(
                onClick = { createTicketLauncher.launch(Unit) },
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
fun TicketListContent(
    tickets: Flow<PagingData<Ticket>>,
    isEmpty: Flow<Boolean>,
    listState: LazyListState
) {
    LazyList(
        data = tickets,
        getKey = { it.id },
        isEmpty = isEmpty,
        emptyTitleRes = R.string.empty_state_ticket_title,
        emptyTextRes = R.string.empty_state_ticket_text,
        listState = listState
    ) { ticket -> TicketItem(ticket) }
}

@Preview(showBackground = true)
@Composable
fun TicketListPreview() {
    PreviewTheme {
        TicketListContent(
            tickets = flowOf(
                PagingData.from(
                    listOf(
                        Ticket("KlimaTicket", 109500, LocalDate.now(), LocalDate.now(), System.currentTimeMillis())
                    )
                )
            ),
            isEmpty = flowOf(false),
            rememberLazyListState()
        )
    }
}
