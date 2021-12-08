package com.patloew.oeffitracker.ui.trip.create

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.data.model.OptionalTripField
import com.patloew.oeffitracker.data.model.TransportType
import com.patloew.oeffitracker.ui.amountVisualTransformation
import com.patloew.oeffitracker.ui.common.Chip
import com.patloew.oeffitracker.ui.common.ClickActionTextField
import com.patloew.oeffitracker.ui.common.FlowRowTextField
import com.patloew.oeffitracker.ui.common.NavigationBackIcon
import com.patloew.oeffitracker.ui.common.SectionHeader
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
fun CreateTripScreen(
    navigationAction: () -> Unit,
    onDateClick: () -> Unit,
    onDurationClick: () -> Unit,
    onDelayClick: () -> Unit,
    viewModel: CreateTripViewModel
) {
    val scaffoldState = rememberScaffoldState()

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = viewModel.toolbarTitleRes)) },
                    navigationIcon = { NavigationBackIcon { navigationAction() } }
                )
            },
            content = {
                CreateTripContent(
                    onDateClick,
                    onDurationClick,
                    onDurationClear = { viewModel.duration.value = null },
                    onDelayClick,
                    onDelayClear = { viewModel.delay.value = null },
                    viewModel::onTypeClick,
                    viewModel::onCreate,
                    viewModel::setFare,
                    viewModel::setAdditionalCosts,
                    viewModel::setDistance,
                    viewModel.saveEnabled,
                    viewModel.optionalTripFieldEnabledMap,
                    viewModel.startCity,
                    viewModel.endCity,
                    viewModel.durationString,
                    viewModel.delayString,
                    viewModel.dateString,
                    viewModel.types,
                    viewModel.initialFare,
                    viewModel.initialAdditionalCosts,
                    viewModel.initialDistance,
                    viewModel.notes,
                    viewModel.buttonTextRes
                )
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateTripContent(
    onDateClick: () -> Unit,
    onDurationClick: () -> Unit,
    onDurationClear: () -> Unit,
    onDelayClick: () -> Unit,
    onDelayClear: () -> Unit,
    onTypeClick: (TransportType) -> Unit,
    onCreateClick: () -> Unit,
    setFare: (String) -> Boolean,
    setAdditionalCosts: (String) -> Boolean,
    setDistance: (String) -> Boolean,
    saveEnabled: Flow<Boolean>,
    optionalTripFieldEnabledMap: Map<OptionalTripField, StateFlow<Boolean>>,
    startCityStateFlow: MutableStateFlow<String>,
    endCityStateFlow: MutableStateFlow<String>,
    durationFlow: Flow<String>,
    delayFlow: Flow<String>,
    dateFlow: Flow<String>,
    types: Flow<Map<TransportType, Boolean>>,
    initialFare: String,
    initialAdditionalCosts: String,
    initialDistance: String,
    notesFlow: MutableStateFlow<String>,
    @StringRes buttonTextRes: Int
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
            .padding(horizontal = 16.dp)
    ) {
        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberScrollState()
        val keyboardController = LocalSoftwareKeyboardController.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            @Composable
            fun isOptionalFieldEnabled(field: OptionalTripField): Boolean =
                optionalTripFieldEnabledMap[field]!!.collectAsState().value

            val isAdditionalCostsEnabled = isOptionalFieldEnabled(OptionalTripField.ADDITIONAL_COSTS)
            val isDistanceEnabled = isOptionalFieldEnabled(OptionalTripField.DISTANCE)

            var fare by remember { mutableStateOf(initialFare) }
            var additionalCosts by remember { mutableStateOf(initialAdditionalCosts) }
            var distance by remember { mutableStateOf(initialDistance) }
            val endCityFocusRequester = FocusRequester()
            val fareFocusRequester = FocusRequester()
            val additionalCostsFocusRequester = FocusRequester()
            val distanceFocusRequester = FocusRequester()

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                value = startCityStateFlow.collectAsState().value,
                onValueChange = { startCityStateFlow.value = it },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { endCityFocusRequester.requestFocus() }),
                label = { Text(stringResource(id = R.string.label_start_city)) }
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .focusRequester(endCityFocusRequester),
                value = endCityStateFlow.collectAsState().value,
                onValueChange = { endCityStateFlow.value = it },
                leadingIcon = {
                    Icon(
                        painterResource(id = R.drawable.ic_flag),
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { fareFocusRequester.requestFocus() }),
                label = { Text(stringResource(id = R.string.label_end_city)) }
            )

            ClickActionTextField(
                modifier = Modifier.padding(top = 8.dp),
                onClick = onDateClick,
                textFlow = dateFlow,
                iconRes = R.drawable.ic_calendar,
                labelRes = R.string.label_date
            )

            SectionHeader(
                text = stringResource(id = R.string.create_trip_hint_optional_fields),
                modifier = Modifier.padding(top = 16.dp)
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(fareFocusRequester),
                value = fare,
                onValueChange = { newValue -> if (setFare(newValue)) fare = newValue },
                maxLines = 1,
                leadingIcon = {
                    Icon(
                        painterResource(id = R.drawable.ic_fare),
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                },
                visualTransformation = amountVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onNext = {
                    when {
                        isAdditionalCostsEnabled -> additionalCostsFocusRequester.requestFocus()
                        isDistanceEnabled -> distanceFocusRequester.requestFocus()
                        else -> keyboardController?.hide()
                    }
                }),
                label = { Text(stringResource(id = R.string.label_fare)) }
            )

            if (isAdditionalCostsEnabled) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .focusRequester(additionalCostsFocusRequester),
                    value = additionalCosts,
                    onValueChange = { newValue -> if (setAdditionalCosts(newValue)) additionalCosts = newValue },
                    maxLines = 1,
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.ic_price_plus),
                            contentDescription = null,
                            tint = MaterialTheme.colors.primary
                        )
                    },
                    visualTransformation = amountVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onNext = {
                        when {
                            isDistanceEnabled -> distanceFocusRequester.requestFocus()
                            else -> keyboardController?.hide()
                        }
                    }),
                    label = { Text(stringResource(id = R.string.label_additional_costs)) }
                )
            }

            if (isDistanceEnabled) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .focusRequester(distanceFocusRequester),
                    value = distance,
                    onValueChange = { newValue -> if (setDistance(newValue)) distance = newValue },
                    maxLines = 1,
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.ic_distance),
                            contentDescription = null,
                            tint = MaterialTheme.colors.primary
                        )
                    },
                    visualTransformation = {
                        val suffix = if (it.text.isNotEmpty()) " km" else ""
                        TransformedText(AnnotatedString("${it.text}$suffix"), OffsetMapping.Identity)
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onNext = { keyboardController?.hide() }),
                    label = { Text(stringResource(id = R.string.label_distance)) }
                )
            }

            if (isOptionalFieldEnabled(OptionalTripField.DURATION)) {
                ClickActionTextField(
                    modifier = Modifier.padding(top = 8.dp),
                    onClick = onDurationClick,
                    onClear = onDurationClear,
                    textFlow = durationFlow,
                    iconRes = R.drawable.ic_clock,
                    labelRes = R.string.label_duration
                )
            }

            if (isOptionalFieldEnabled(OptionalTripField.DELAY)) {
                ClickActionTextField(
                    modifier = Modifier.padding(top = 8.dp),
                    onClick = onDelayClick,
                    onClear = onDelayClear,
                    textFlow = delayFlow,
                    iconRes = R.drawable.ic_delay,
                    labelRes = R.string.label_delay
                )
            }

            if (isOptionalFieldEnabled(OptionalTripField.TYPE)) {
                FlowRowTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    types.collectAsState(initial = emptyMap()).value.entries.forEach { (type, selected) ->
                        Chip(text = stringResource(id = type.stringRes), isSelected = selected) { onTypeClick(type) }
                    }
                }
            }

            if (isOptionalFieldEnabled(OptionalTripField.NOTES)) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .onFocusChanged { state ->
                            if (state.isFocused) {
                                coroutineScope.launch {
                                    delay(300)
                                    scrollState.animateScrollTo(Integer.MAX_VALUE)
                                }
                            }
                        },
                    value = notesFlow.collectAsState().value,
                    onValueChange = { notesFlow.value = it },
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.ic_note),
                            contentDescription = null,
                            tint = MaterialTheme.colors.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { keyboardController?.hide() }),
                    label = { Text(stringResource(id = R.string.label_note)) }
                )
            }

            Spacer(Modifier.padding(bottom = 92.dp))
        }

        Button(
            onClick = { onCreateClick() },
            enabled = saveEnabled.collectAsState(initial = false).value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(44.dp)
                .align(Alignment.BottomCenter)
        ) { Text(stringResource(id = buttonTextRes), fontSize = 16.sp) }
    }

}