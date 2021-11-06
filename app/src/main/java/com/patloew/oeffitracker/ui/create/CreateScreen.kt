package com.patloew.oeffitracker.ui.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.ui.PreviewTheme
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

@Composable
fun CreateScreen(
    navigationAction: () -> Unit,
    onDateClick: () -> Unit,
    onCreateClick: () -> Unit,
    viewModel: CreateViewModel
) {
    val scaffoldState = rememberScaffoldState()

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.toolbar_title_create)) },
                    navigationIcon = {
                        Icon(
                            Icons.Filled.ArrowBack,
                            stringResource(id = R.string.action_back),
                            tint = MaterialTheme.colors.onPrimary,
                            modifier = Modifier
                                .clickable { navigationAction() }
                                .padding(16.dp)
                        )
                    }
                )
            },
            content = { CreateContent(onDateClick, onCreateClick, viewModel.startCity, viewModel.endCity) }
        )
    }
}

@Composable
fun CreateContent(
    onDateClick: () -> Unit,
    onCreateClick: () -> Unit,
    startCityStateFlow: MutableStateFlow<String>,
    endCityStateFlow: MutableStateFlow<String>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        var fare by remember { mutableStateOf(TextFieldValue()) }
        var date by remember { mutableStateOf(TextFieldValue()) }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = startCityStateFlow.collectAsState().value,
            onValueChange = { startCityStateFlow.value = it },
            maxLines = 1,
            leadingIcon = {
                Icon(
                    Icons.Filled.Place,
                    stringResource(id = R.string.accessibility_icon_place),
                    tint = MaterialTheme.colors.primary
                )
            },
            label = { Text(stringResource(id = R.string.label_start_city)) }
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            value = endCityStateFlow.collectAsState().value,
            onValueChange = { endCityStateFlow.value = it },
            maxLines = 1,
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.ic_flag),
                    stringResource(id = R.string.accessibility_icon_place),
                    tint = MaterialTheme.colors.primary
                )
            },
            label = { Text(stringResource(id = R.string.label_end_city)) }
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            value = fare,
            onValueChange = { fare = it },
            maxLines = 1,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.ic_fare),
                    stringResource(id = R.string.accessibility_icon_place),
                    tint = MaterialTheme.colors.primary
                )
            },
            label = { Text(stringResource(id = R.string.label_fare)) }
        )

        Box(modifier = Modifier.padding(top = 16.dp)) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = date,
                onValueChange = { date = it },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    Icon(
                        painterResource(id = R.drawable.ic_calendar),
                        stringResource(id = R.string.accessibility_icon_place),
                        tint = MaterialTheme.colors.primary
                    )
                },
                label = { Text(stringResource(id = R.string.label_date)) }
            )

            Box(modifier = Modifier
                .matchParentSize()
                .clickable { onDateClick() })

        }

        Button(
            onClick = { onCreateClick() },
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) { Text(stringResource(id = R.string.button_add)) }
    }

}

@Preview(showBackground = true)
@Composable
fun CreatePreview() {
    PreviewTheme {
        CreateContent({ }, { }, MutableStateFlow(""), MutableStateFlow(""))
    }
}