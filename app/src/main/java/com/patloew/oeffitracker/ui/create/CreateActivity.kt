package com.patloew.oeffitracker.ui.create

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.data.model.Trip
import com.patloew.oeffitracker.data.repository.TripDao
import com.patloew.oeffitracker.ui.PreviewTheme
import com.patloew.oeffitracker.ui.theme.OeffiTrackerTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.security.SecureRandom
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

class CreateActivity : FragmentActivity() {

    object Contract : ActivityResultContract<Unit, Boolean>() {
        override fun createIntent(context: Context, input: Unit): Intent =
            Intent(context, CreateActivity::class.java)

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean =
            resultCode == Activity.RESULT_OK
    }

    private val viewModel: CreateViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OeffiTrackerTheme {
                CreateScreen(
                    navigationAction = { finish() },
                    onDateClick = {
                        MaterialDatePicker.Builder.datePicker()
                            .setTitleText("")
                            .setSelection(System.currentTimeMillis())
                            .setCalendarConstraints(
                                CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now()).build()
                            )
                            .build()
                            .show(supportFragmentManager, null)
                    },
                    onCreateClick = { viewModel.onCreate() },
                    viewModel
                )
            }
        }

        lifecycleScope.launch {
            viewModel.finishEvent.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
        }
    }
}

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

class CreateViewModel(
    private val tripDao: TripDao
) : ViewModel() {

    val startCity: MutableStateFlow<String> = MutableStateFlow("")
    val endCity: MutableStateFlow<String> = MutableStateFlow("")

    private val finishChannel: Channel<Unit> = Channel(Channel.CONFLATED)
    val finishEvent: Flow<Unit> = finishChannel.receiveAsFlow()

    fun onCreate() {
        viewModelScope.launch {
            tripDao.update(
                Trip(
                    startCity = startCity.value,
                    endCity = endCity.value,
                    price = SecureRandom().nextInt(10000),
                    date = LocalDate.now(),
                    createdTimestamp = System.currentTimeMillis()
                )
            )
            finishChannel.send(Unit)
        }
    }
}