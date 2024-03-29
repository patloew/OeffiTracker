package com.patloew.oeffitracker.ui.ticket.create

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.data.model.Ticket
import com.patloew.oeffitracker.ui.amountVisualTransformation
import com.patloew.oeffitracker.ui.common.ClickActionTextField
import com.patloew.oeffitracker.ui.common.NavigationBackIcon
import com.patloew.oeffitracker.ui.ticket.list.validityPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

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
fun CreateTicketScreen(
    navigationAction: () -> Unit,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    onCreateClick: () -> Unit,
    viewModel: CreateTicketViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(id = viewModel.toolbarTitleRes)) },
                navigationIcon = { NavigationBackIcon { navigationAction() } },
                scrollBehavior = scrollBehavior,
            )
        },
        content = { paddingValues ->
            CreateTicketContent(
                Modifier
                    .padding(paddingValues)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                onStartDateClick,
                onEndDateClick,
                onCreateClick,
                viewModel::setPrice,
                viewModel::setDeduction,
                viewModel.saveEnabled,
                viewModel.endDateBeforeStartDate,
                viewModel.overlappingTicket,
                viewModel.name,
                viewModel.startDateString,
                viewModel.endDateString,
                viewModel.initialPrice,
                viewModel.initialDeduction,
                viewModel.buttonTextRes
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketContent(
    modifier: Modifier = Modifier,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    onCreateClick: () -> Unit,
    setPrice: (String) -> Boolean,
    setDeduction: (String) -> Boolean,
    saveEnabled: Flow<Boolean>,
    endDateBeforeStartDate: Flow<Boolean>,
    overlappingTicket: Flow<Ticket?>,
    nameStateFlow: MutableStateFlow<String>,
    startDateFlow: Flow<String>,
    endDateFlow: Flow<String>,
    initialPrice: String,
    initialDeduction: String,
    @StringRes buttonTextRes: Int
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        var price by remember { mutableStateOf(initialPrice) }
        var deduction by remember { mutableStateOf(initialDeduction) }
        val priceFocusRequester = FocusRequester()
        val deductionFocusRequester = FocusRequester()

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = nameStateFlow.collectAsState().value,
            onValueChange = { nameStateFlow.value = it },
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.ic_receipt),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
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
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            visualTransformation = amountVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(onNext = { deductionFocusRequester.requestFocus() }),
            label = { Text(stringResource(id = R.string.label_ticket_price)) }
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .focusRequester(deductionFocusRequester),
            value = deduction,
            onValueChange = { newValue -> if (setDeduction(newValue)) deduction = newValue },
            maxLines = 1,
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.ic_deduction),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            visualTransformation = amountVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(onNext = { onStartDateClick() }),
            label = { Text(stringResource(id = R.string.label_ticket_deduction)) }
        )

        Text(
            text = stringResource(id = R.string.ticket_deduction_hint),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 8.dp, start = 4.dp, end = 4.dp)
        )

        ClickActionTextField(
            modifier = Modifier.padding(top = 16.dp),
            onClick = onStartDateClick,
            textFlow = startDateFlow,
            iconRes = R.drawable.ic_calendar,
            labelRes = R.string.label_start_date
        )

        ClickActionTextField(
            modifier = Modifier.padding(top = 16.dp),
            onClick = onEndDateClick,
            textFlow = endDateFlow,
            iconRes = R.drawable.ic_calendar,
            labelRes = R.string.label_end_date
        )

        val overlappingTicketValue = overlappingTicket.collectAsState(initial = null).value
        val (validityHintText, validityHintColor) = when {
            endDateBeforeStartDate.collectAsState(initial = false).value ->
                stringResource(id = R.string.ticket_trip_validity_hint_enddate_startdate_error) to
                    MaterialTheme.colorScheme.error
            overlappingTicketValue != null ->
                stringResource(
                    id = R.string.ticket_trip_validity_hint_overlap_error,
                    overlappingTicketValue.name,
                    overlappingTicketValue.validityPeriod
                ) to MaterialTheme.colorScheme.error
            else -> stringResource(id = R.string.ticket_trip_validity_hint) to Color.Unspecified
        }

        Text(
            text = validityHintText,
            style = MaterialTheme.typography.labelSmall,
            color = validityHintColor,
            modifier = Modifier.padding(top = 8.dp, start = 4.dp, end = 4.dp)
        )

        Button(
            onClick = { onCreateClick() },
            enabled = saveEnabled.collectAsState(initial = false).value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .height(44.dp)

        ) { Text(stringResource(id = buttonTextRes), fontSize = 16.sp) }
    }

}