package com.patloew.oeffitracker.ui.ticket.create

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
import com.patloew.oeffitracker.data.model.Ticket
import com.patloew.oeffitracker.data.repository.TicketDao
import com.patloew.oeffitracker.ui.checkAndSetAmount
import com.patloew.oeffitracker.ui.dateFormat
import com.patloew.oeffitracker.ui.formatAmount
import com.patloew.oeffitracker.ui.showDatePicker
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

private const val EXTRA_EDIT_TICKET = "editTicket"

class CreateTicketActivity : FragmentActivity() {

    object CreateContract : ActivityResultContract<Unit, Boolean>() {
        override fun createIntent(context: Context, input: Unit): Intent =
            Intent(context, CreateTicketActivity::class.java)

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean =
            resultCode == Activity.RESULT_OK
    }

    object EditContract : ActivityResultContract<Ticket, Boolean>() {
        override fun createIntent(context: Context, input: Ticket): Intent =
            Intent(context, CreateTicketActivity::class.java).apply {
                putExtra(EXTRA_EDIT_TICKET, input)
            }

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean =
            resultCode == Activity.RESULT_OK
    }

    private val viewModelFactory: CreateTicketViewModel.Factory by inject()
    private val viewModel: CreateTicketViewModel by viewModelFactory {
        val editTicket: Ticket? = intent.getParcelableExtra(EXTRA_EDIT_TICKET)
        when {
            editTicket != null -> viewModelFactory.edit(editTicket)
            else -> viewModelFactory.create()
        }
    }

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
    private val editTicket: Ticket?
) : ViewModel() {

    @StringRes
    val toolbarTitleRes: Int = if (editTicket == null) {
        R.string.toolbar_title_create_ticket
    } else {
        R.string.toolbar_title_edit_ticket
    }

    @StringRes
    val buttonTextRes: Int = if (editTicket == null) {
        R.string.button_add
    } else {
        R.string.button_edit
    }

    val name: MutableStateFlow<String> = MutableStateFlow(editTicket?.name ?: "")

    val startDate: MutableStateFlow<LocalDate> = MutableStateFlow(editTicket?.startDate ?: LocalDate.now())
    val startDateString: Flow<String> = startDate.map { dateFormat.format(it) }

    val endDate: MutableStateFlow<LocalDate> =
        MutableStateFlow(editTicket?.endDate ?: LocalDate.now().plusYears(1).minusDays(1))
    val endDateString: Flow<String> = endDate.map { dateFormat.format(it) }

    val overlappingTicket: Flow<Ticket?> = combine(startDate, endDate) { startDate, endDate ->
        ticketDao.getFirstTwoOverlappingValidityTickets(startDate, endDate)
            .firstOrNull { it.id != editTicket?.id } // Don't check overlapping of self when editing
    }

    val endDateBeforeStartDate: Flow<Boolean> =
        combine(startDate, endDate) { startDate, endDate -> endDate <= startDate }

    private val price: MutableStateFlow<Int?> = MutableStateFlow(editTicket?.price)
    val initialPrice = editTicket?.price.formatAmount() ?: ""

    val saveEnabled: Flow<Boolean> = combine(
        name,
        price,
        startDate,
        endDate,
        overlappingTicket
    ) { name, price, startDate, endDate, overlappingTicket ->
        name.isNotEmpty() && price != null && price > 0 && startDate < endDate && overlappingTicket == null
    }

    private val finishChannel: Channel<Unit> = Channel(Channel.CONFLATED)
    val finishEvent: Flow<Unit> = finishChannel.receiveAsFlow()

    fun onCreate() {
        viewModelScope.launch {
            if (editTicket != null) {
                ticketDao.update(
                    editTicket.copy(
                        name = name.value.trim(),
                        price = price.value!!,
                        startDate = startDate.value,
                        endDate = endDate.value
                    )
                )
            } else {
                ticketDao.insert(
                    Ticket(
                        name = name.value.trim(),
                        price = price.value!!,
                        startDate = startDate.value,
                        endDate = endDate.value,
                        createdTimestamp = System.currentTimeMillis()
                    )
                )
            }
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

    class Factory(private val ticketDao: TicketDao) {
        fun create() = CreateTicketViewModel(ticketDao, editTicket = null)
        fun edit(ticket: Ticket) = CreateTicketViewModel(ticketDao, editTicket = ticket)
    }
}