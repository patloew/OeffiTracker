package com.patloew.oeffitracker.ui.ticket.create

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
import com.patloew.oeffitracker.data.model.Ticket
import com.patloew.oeffitracker.data.repository.SettingsRepo
import com.patloew.oeffitracker.data.repository.TicketDao
import com.patloew.oeffitracker.ui.checkAndSetAmount
import com.patloew.oeffitracker.ui.dateFormat
import com.patloew.oeffitracker.ui.showDatePicker
import com.patloew.oeffitracker.ui.theme.OeffiTrackerTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
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

class CreateTicketActivity : FragmentActivity() {

    object Contract : ActivityResultContract<Unit, Boolean>() {
        override fun createIntent(context: Context, input: Unit): Intent =
            Intent(context, CreateTicketActivity::class.java)

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean =
            resultCode == Activity.RESULT_OK
    }

    private val viewModel: CreateTicketViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OeffiTrackerTheme {
                CreateTicketScreen(
                    navigationAction = { finish() },
                    onStartDateClick = {
                        showDatePicker(
                            preSelected = viewModel.startDate.value,
                            supportFragmentManager
                        ) { selectedDate ->
                            viewModel.startDate.value = selectedDate
                        }
                    },
                    onEndDateClick = {
                        showDatePicker(preSelected = viewModel.endDate.value, supportFragmentManager) { selectedDate ->
                            viewModel.endDate.value = selectedDate
                        }
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

class CreateTicketViewModel(
    private val ticketDao: TicketDao,
    private val settingsRepo: SettingsRepo
) : ViewModel() {

    val name: MutableStateFlow<String> = MutableStateFlow("")

    val startDate: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now().minusYears(1).plusDays(1))
    val startDateString: Flow<String> = startDate.map { dateFormat.format(it) }

    val endDate: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    val endDateString: Flow<String> = endDate.map { dateFormat.format(it) }

    private val price: MutableStateFlow<Int?> = MutableStateFlow(null)
    val initialPrice = ""

    val saveEnabled: Flow<Boolean> = combine(name, price) { name, price ->
        name.isNotEmpty() && price != null && price > 0
    }

    private val finishChannel: Channel<Unit> = Channel(Channel.CONFLATED)
    val finishEvent: Flow<Unit> = finishChannel.receiveAsFlow()

    fun onCreate() {
        viewModelScope.launch {
            val ticketId = ticketDao.insert(
                Ticket(
                    name = name.value,
                    price = price.value!!,
                    startDate = startDate.value,
                    endDate = endDate.value,
                    createdTimestamp = System.currentTimeMillis()
                )
            )
            settingsRepo.setHighlightedTicketId(ticketId)
            finishChannel.send(Unit)
        }
    }

    /**
     * Parses and validates price and sets it if valid.
     *
     * @return true if price is valid
     */
    fun setPrice(priceString: String): Boolean =
        checkAndSetAmount(priceString) { newPrice -> price.value = newPrice }

}