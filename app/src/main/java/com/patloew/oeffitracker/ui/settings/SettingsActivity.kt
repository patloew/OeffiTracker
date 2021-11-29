package com.patloew.oeffitracker.ui.settings

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patloew.oeffitracker.data.export.CsvExporter
import com.patloew.oeffitracker.data.model.OptionalTripField
import com.patloew.oeffitracker.data.repository.SettingsRepo
import com.patloew.oeffitracker.ui.theme.OeffiTrackerTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

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

class SettingsActivity : FragmentActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OeffiTrackerTheme {
                SettingsScreen(
                    navigationAction = { finish() },
                    viewModel = viewModel
                )
            }
        }
    }
}

class SettingsViewModel(
    private val settingsRepo: SettingsRepo,
    private val csvExporter: CsvExporter
) : ViewModel() {

    val optionalTripFieldEnabledMap = settingsRepo.optionalTripFieldEnabledMap
    val includeDeductionsInProgress = settingsRepo.includeDeductionsInProgress

    private val csvExportChannel: Channel<Boolean> = Channel(Channel.CONFLATED)
    val csvExportEvent: Flow<Boolean> = csvExportChannel.receiveAsFlow()

    fun setOptionalTripFieldEnabled(field: OptionalTripField, enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setOptionalTripFieldEnabled(field, enabled) }
    }

    fun setIncludeDeductionsInProgress(include: Boolean) {
        viewModelScope.launch { settingsRepo.setIncludeDeductionsInProgress(include) }
    }

    fun exportCsv(uri: Uri) {
        viewModelScope.launch {
            val success = csvExporter.exportTo(uri)
            csvExportChannel.send(success)
        }
    }
}