package com.patloew.oeffitracker.ui.create

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.patloew.oeffitracker.data.model.Trip
import com.patloew.oeffitracker.data.repository.TripDao
import com.patloew.oeffitracker.ui.dateFormat
import com.patloew.oeffitracker.ui.theme.OeffiTrackerTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

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
                            .setSelection(viewModel.date.value.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
                            .setCalendarConstraints(
                                CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now()).build()
                            )
                            .build()
                            .apply {
                                addOnPositiveButtonClickListener { timeMillis ->
                                    viewModel.date.value =
                                        Instant.ofEpochMilli(timeMillis).atZone(ZoneOffset.UTC).toLocalDate()
                                }
                            }
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

class CreateViewModel(
    private val tripDao: TripDao
) : ViewModel() {

    val startCity: MutableStateFlow<String> = MutableStateFlow("")
    val endCity: MutableStateFlow<String> = MutableStateFlow("")

    val date: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    val dateString: Flow<String> = date.map { dateFormat.format(it) }

    private val fare: MutableStateFlow<Int?> = MutableStateFlow(null)
    val initialFare = ""

    private val finishChannel: Channel<Unit> = Channel(Channel.CONFLATED)
    val finishEvent: Flow<Unit> = finishChannel.receiveAsFlow()

    fun onCreate() {
        viewModelScope.launch {
            tripDao.insert(
                Trip(
                    startCity = startCity.value,
                    endCity = endCity.value,
                    fare = fare.value!!,
                    date = date.value,
                    createdTimestamp = System.currentTimeMillis()
                )
            )
            finishChannel.send(Unit)
        }
    }

    /**
     * Parses and validates fare and sets it if valid.
     *
     * @return true if fare is valid
     */
    fun setFare(fareString: String): Boolean {
        val newFare = fareString.replace(',', '.')
        return when {
            newFare.isEmpty() -> {
                fare.value = null
                true
            }
            newFare.matches(Regex("^\\d+(\\.\\d{0,2})?$")) ->
                newFare.toFloatOrNull()?.let { newFareFloat ->
                    fare.value = newFareFloat.times(100).toInt()
                    true
                } ?: false
            else -> false
        }
    }
}