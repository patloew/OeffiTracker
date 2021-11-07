package com.patloew.oeffitracker.ui.ticket.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.data.model.Ticket
import com.patloew.oeffitracker.ui.PreviewTheme
import com.patloew.oeffitracker.ui.common.DateTextField
import com.patloew.oeffitracker.ui.ticket.list.validityPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

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
fun CreateTicketScreen(
    navigationAction: () -> Unit,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    onCreateClick: () -> Unit,
    viewModel: CreateTicketViewModel
) {
    val scaffoldState = rememberScaffoldState()

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.toolbar_title_create_ticket)) },
                    navigationIcon = {
                        Icon(
                            Icons.Filled.ArrowBack,
                            stringResource(id = R.string.action_back),
                            tint = MaterialTheme.colors.onPrimary,
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(CircleShape)
                                .clickable { navigationAction() }
                                .padding(8.dp)
                        )
                    }
                )
            },
            content = {
                CreateTicketContent(
                    onStartDateClick,
                    onEndDateClick,
                    onCreateClick,
                    viewModel::setPrice,
                    viewModel.saveEnabled,
                    viewModel.overlappingTicket,
                    viewModel.name,
                    viewModel.startDateString,
                    viewModel.endDateString,
                    viewModel.initialPrice
                )
            }
        )
    }
}

@Composable
fun CreateTicketContent(
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    onCreateClick: () -> Unit,
    setPrice: (String) -> Boolean,
    saveEnabled: Flow<Boolean>,
    overlappingTicket: Flow<Ticket?>,
    nameStateFlow: MutableStateFlow<String>,
    startDateFlow: Flow<String>,
    endDateFlow: Flow<String>,
    initialPrice: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        var price by remember { mutableStateOf(initialPrice) }
        val priceFocusRequester = FocusRequester()

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = nameStateFlow.collectAsState().value,
            onValueChange = { nameStateFlow.value = it },
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.ic_receipt),
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { priceFocusRequester.requestFocus() }),
            label = { Text(stringResource(id = R.string.label_ticket_name)) }
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .focusRequester(priceFocusRequester),
            value = price,
            onValueChange = { newValue -> if (setPrice(newValue)) price = newValue },
            maxLines = 1,
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.ic_fare),
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary
                )
            },
            visualTransformation = { TransformedText(AnnotatedString("${it.text} €"), OffsetMapping.Identity) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(onNext = { onStartDateClick() }),
            label = { Text(stringResource(id = R.string.label_ticket_price)) }
        )

        DateTextField(
            onDateClick = onStartDateClick,
            dateStringFlow = startDateFlow,
            iconRes = R.drawable.ic_calendar,
            labelRes = R.string.label_start_date
        )

        DateTextField(
            onDateClick = onEndDateClick,
            dateStringFlow = endDateFlow,
            iconRes = R.drawable.ic_calendar,
            labelRes = R.string.label_end_date
        )

        val overlappingTicketValue = overlappingTicket.collectAsState(initial = null).value
        if (overlappingTicketValue != null) {
            Text(
                text = stringResource(
                    id = R.string.ticket_trip_validity_hint_error,
                    overlappingTicketValue.name,
                    overlappingTicketValue.validityPeriod
                ),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(top = 16.dp, start = 4.dp, end = 4.dp)
            )
        } else {
            Text(
                text = stringResource(id = R.string.ticket_trip_validity_hint),
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(top = 16.dp, start = 4.dp, end = 4.dp)
            )
        }

        Button(
            onClick = { onCreateClick() },
            enabled = saveEnabled.collectAsState(initial = false).value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) { Text(stringResource(id = R.string.button_add)) }
    }

}

@Preview(showBackground = true)
@Composable
fun CreateTicketPreview() {
    PreviewTheme {
        CreateTicketContent(
            { },
            { },
            { },
            { true },
            flowOf(false),
            flowOf(null),
            MutableStateFlow(""),
            flowOf(""),
            flowOf(""),
            ""
        )
    }
}