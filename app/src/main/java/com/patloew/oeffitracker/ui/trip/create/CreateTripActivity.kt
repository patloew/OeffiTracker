package com.patloew.oeffitracker.ui.trip.create

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.data.model.TransportType
import com.patloew.oeffitracker.data.model.Trip
import com.patloew.oeffitracker.data.repository.TripDao
import com.patloew.oeffitracker.ui.checkAndSetAmount
import com.patloew.oeffitracker.ui.dateFormat
import com.patloew.oeffitracker.ui.formatDuration
import com.patloew.oeffitracker.ui.showDatePicker
import com.patloew.oeffitracker.ui.showDurationPicker
import com.patloew.oeffitracker.ui.theme.OeffiTrackerTheme
import com.patloew.oeffitracker.ui.viewModelFactory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.time.Duration
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

private const val EXTRA_EDIT_TRIP = "editTrip"
private const val EXTRA_TEMPLATE_TRIP = "templateTrip"

class CreateTripActivity : FragmentActivity() {

    object CreateContract : ActivityResultContract<Unit, Boolean>() {
        override fun createIntent(context: Context, input: Unit): Intent =
            Intent(context, CreateTripActivity::class.java)

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean =
            resultCode == Activity.RESULT_OK
    }

    object CreateFromTemplateContract : ActivityResultContract<Trip, Boolean>() {
        override fun createIntent(context: Context, input: Trip): Intent =
            Intent(context, CreateTripActivity::class.java).apply {
                putExtra(EXTRA_TEMPLATE_TRIP, input)
            }

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean =
            resultCode == Activity.RESULT_OK
    }

    object EditContract : ActivityResultContract<Trip, Boolean>() {
        override fun createIntent(context: Context, input: Trip): Intent =
            Intent(context, CreateTripActivity::class.java).apply {
                putExtra(EXTRA_EDIT_TRIP, input)
            }

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean =
            resultCode == Activity.RESULT_OK
    }

    private val viewModelFactory: CreateTripViewModel.Factory by inject()
    private val viewModel: CreateTripViewModel by viewModelFactory {
        val editTrip: Trip? = intent.getParcelableExtra(EXTRA_EDIT_TRIP)
        val templateTrip: Trip? = intent.getParcelableExtra(EXTRA_TEMPLATE_TRIP)
        when {
            editTrip != null -> viewModelFactory.edit(editTrip)
            templateTrip != null -> viewModelFactory.createFromTemplate(templateTrip)
            else -> viewModelFactory.create()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OeffiTrackerTheme {
                CreateTripScreen(
                    navigationAction = { finish() },
                    onDateClick = {
                        showDatePicker(
                            preSelected = viewModel.date.value,
                            fragmentManager = supportFragmentManager
                        ) { selectedDate -> viewModel.date.value = selectedDate }
                    },
                    onDurationClick = {
                        showDurationPicker(
                            preSelected = viewModel.duration.value ?: Duration.ZERO,
                            titleText = resources.getString(R.string.duration_picker_title_duration),
                            fragmentManager = supportFragmentManager
                        ) { selectedDuration -> viewModel.duration.value = selectedDuration }
                    },
                    onDelayClick = {
                        showDurationPicker(
                            preSelected = viewModel.delay.value ?: Duration.ZERO,
                            titleText = resources.getString(R.string.duration_picker_title_delay),
                            fragmentManager = supportFragmentManager
                        ) { selectedDuration -> viewModel.delay.value = selectedDuration }
                    },
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


class CreateTripViewModel(
    private val tripDao: TripDao,
    template: Trip?,
    private val editTrip: Trip?
) : ViewModel() {

    @StringRes
    val toolbarTitleRes: Int = if (editTrip == null) {
        R.string.toolbar_title_create_trip
    } else {
        R.string.toolbar_title_edit_trip
    }

    @StringRes
    val buttonTextRes: Int = if (editTrip == null) {
        R.string.button_add
    } else {
        R.string.button_edit
    }

    val startCity: MutableStateFlow<String> = MutableStateFlow(template?.startCity ?: editTrip?.startCity ?: "")
    val endCity: MutableStateFlow<String> = MutableStateFlow(template?.endCity ?: editTrip?.endCity ?: "")

    val date: MutableStateFlow<LocalDate> = MutableStateFlow(template?.date ?: editTrip?.date ?: LocalDate.now())
    val dateString: Flow<String> = date.map { dateFormat.format(it) }

    private val fare: MutableStateFlow<Int?> = MutableStateFlow(template?.fare ?: editTrip?.fare)
    val initialFare = template?.floatFareString ?: editTrip?.floatFareString ?: ""

    val duration: MutableStateFlow<Duration?> = MutableStateFlow(template?.duration ?: editTrip?.duration)
    val durationString: Flow<String> = duration.map { it?.let(::formatDuration) ?: "" }

    val delay: MutableStateFlow<Duration?> = MutableStateFlow(template?.delay ?: editTrip?.delay)
    val delayString: Flow<String> = delay.map { it?.let(::formatDuration) ?: "" }

    private val distance: MutableStateFlow<Float?> = MutableStateFlow(template?.distance ?: editTrip?.distance)
    val initialDistance = template?.floatDistanceString ?: editTrip?.floatDistanceString ?: ""

    val types: MutableStateFlow<Map<TransportType, Boolean>> =
        MutableStateFlow(typesWithSelected(template?.type ?: editTrip?.type))
    val selectedTypes: List<TransportType>
        get() = types.value.filterValues { isSelected -> isSelected }.keys.toList()

    val saveEnabled: Flow<Boolean> = combine(startCity, endCity, fare) { startCity, endCity, fare ->
        startCity.isNotEmpty() && endCity.isNotEmpty() && (fare == null || fare > 0)
    }

    private val finishChannel: Channel<Unit> = Channel(Channel.CONFLATED)
    val finishEvent: Flow<Unit> = finishChannel.receiveAsFlow()

    fun onCreate() {
        viewModelScope.launch {
            if (editTrip != null) {
                tripDao.update(
                    editTrip.copy(
                        startCity = startCity.value.trim(),
                        endCity = endCity.value.trim(),
                        fare = fare.value,
                        date = date.value,
                        duration = duration.value,
                        distance = distance.value,
                        delay = delay.value,
                        type = selectedTypes
                    )
                )
            } else {
                tripDao.insert(
                    Trip(
                        startCity = startCity.value.trim(),
                        endCity = endCity.value.trim(),
                        fare = fare.value,
                        date = date.value,
                        duration = duration.value,
                        delay = delay.value,
                        distance = distance.value,
                        type = selectedTypes,
                        createdTimestamp = System.currentTimeMillis()
                    )
                )
            }
            finishChannel.send(Unit)
        }
    }

    fun onTypeClick(type: TransportType) {
        types.value = types.value.toMutableMap().apply { set(type, !getOrDefault(type, false)) }
    }

    private fun typesWithSelected(selected: List<TransportType>?) =
        TransportType.values().associateWith { type -> selected?.contains(type) ?: false }

    fun setDistance(distanceString: String): Boolean = when {
        distanceString.isEmpty() -> {
            distance.value = null
            true
        }
        else -> distanceString.replace(',', '.').toFloatOrNull()?.let {
            distance.value = it
            true
        } ?: false
    }

    /**
     * Parses and validates fare and sets it if valid.
     *
     * @return true if fare is valid
     */
    fun setFare(fareString: String): Boolean =
        checkAndSetAmount(fareString) { newFare -> fare.value = newFare }

    private val Trip.floatDistanceString: String?
        get() = if (distance?.mod(1.0) == 0.0) {
            distance.toInt().toString()
        } else {
            distance?.toString()?.replace('.', ',')
        }

    private val Trip.floatFareString: String?
        get() = if (fare?.mod(100) == 0) {
            floatFare!!.toInt().toString()
        } else {
            floatFare?.toString()?.replace('.', ',')
        }

    class Factory(private val tripDao: TripDao) {
        fun create() = CreateTripViewModel(tripDao, template = null, editTrip = null)
        fun createFromTemplate(template: Trip) = CreateTripViewModel(tripDao, template, editTrip = null)
        fun edit(trip: Trip) = CreateTripViewModel(tripDao, template = null, editTrip = trip)
    }

}