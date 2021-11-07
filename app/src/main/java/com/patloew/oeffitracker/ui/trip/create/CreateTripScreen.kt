package com.patloew.oeffitracker.ui.trip.create

import androidx.annotation.StringRes
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
import androidx.compose.material.icons.filled.Place
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
import com.patloew.oeffitracker.ui.PreviewTheme
import com.patloew.oeffitracker.ui.common.DateTextField
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
fun CreateTripScreen(
    navigationAction: () -> Unit,
    onDateClick: () -> Unit,
    onCreateClick: () -> Unit,
    viewModel: CreateTripViewModel
) {
    val scaffoldState = rememberScaffoldState()

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = viewModel.toolbarTitleRes)) },
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
                CreateTripContent(
                    onDateClick,
                    onCreateClick,
                    viewModel::setFare,
                    viewModel.saveEnabled,
                    viewModel.startCity,
                    viewModel.endCity,
                    viewModel.dateString,
                    viewModel.initialFare,
                    viewModel.buttonTextRes
                )
            }
        )
    }
}

@Composable
fun CreateTripContent(
    onDateClick: () -> Unit,
    onCreateClick: () -> Unit,
    setFare: (String) -> Boolean,
    saveEnabled: Flow<Boolean>,
    startCityStateFlow: MutableStateFlow<String>,
    endCityStateFlow: MutableStateFlow<String>,
    dateFlow: Flow<String>,
    initialFare: String,
    @StringRes buttonTextRes: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        var fare by remember { mutableStateOf(initialFare) }
        val endCityFocusRequester = FocusRequester()
        val fareFocusRequester = FocusRequester()

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
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
                .padding(top = 16.dp)
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

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
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
            visualTransformation = { TransformedText(AnnotatedString("${it.text} €"), OffsetMapping.Identity) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(onNext = { onDateClick() }),
            label = { Text(stringResource(id = R.string.label_fare)) }
        )

        DateTextField(
            onDateClick = onDateClick,
            dateStringFlow = dateFlow,
            iconRes = R.drawable.ic_calendar,
            labelRes = R.string.label_date
        )

        Button(
            onClick = { onCreateClick() },
            enabled = saveEnabled.collectAsState(initial = false).value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) { Text(stringResource(id = buttonTextRes)) }
    }

}

@Preview(showBackground = true)
@Composable
fun CreateTripPreview() {
    PreviewTheme {
        CreateTripContent(
            { },
            { },
            { true },
            flowOf(false),
            MutableStateFlow(""),
            MutableStateFlow(""),
            flowOf(""),
            "",
            0
        )
    }
}