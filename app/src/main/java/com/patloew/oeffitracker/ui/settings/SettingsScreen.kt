package com.patloew.oeffitracker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.data.model.OptionalTripField
import com.patloew.oeffitracker.ui.common.CheckedText
import com.patloew.oeffitracker.ui.common.NavigationBackIcon
import com.patloew.oeffitracker.ui.common.SectionHeader

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
fun SettingsScreen(
    navigationAction: () -> Unit,
    viewModel: SettingsViewModel
) {
    val scaffoldState = rememberScaffoldState()

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.toolbar_title_settings)) },
                    navigationIcon = { NavigationBackIcon { navigationAction() } }
                )
            },
            content = { SettingsContent(viewModel) }
        )
    }
}

@Composable
fun SettingsContent(viewModel: SettingsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SectionHeader(
            text = stringResource(id = R.string.section_settings_optional_trip_fields),
            modifier = Modifier.background(MaterialTheme.colors.surface)
        )

        Text(
            text = stringResource(id = R.string.settings_optional_trip_fields_description),
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.67f),
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        CheckedText(
            iconRes = R.drawable.ic_price_plus,
            text = stringResource(id = R.string.label_additional_costs),
            checkedFlow = viewModel.getOptionalTripFieldEnabled(OptionalTripField.ADDITIONAL_COSTS),
            setCheckedState = { viewModel.setOptionalTripFieldEnabled(OptionalTripField.ADDITIONAL_COSTS, it) }
        )

        CheckedText(
            iconRes = R.drawable.ic_distance,
            text = stringResource(id = R.string.label_distance),
            checkedFlow = viewModel.getOptionalTripFieldEnabled(OptionalTripField.DISTANCE),
            setCheckedState = { viewModel.setOptionalTripFieldEnabled(OptionalTripField.DISTANCE, it) }
        )

        CheckedText(
            iconRes = R.drawable.ic_clock,
            text = stringResource(id = R.string.label_duration),
            checkedFlow = viewModel.getOptionalTripFieldEnabled(OptionalTripField.DURATION),
            setCheckedState = { viewModel.setOptionalTripFieldEnabled(OptionalTripField.DURATION, it) }
        )

        CheckedText(
            iconRes = R.drawable.ic_delay,
            text = stringResource(id = R.string.label_delay),
            checkedFlow = viewModel.getOptionalTripFieldEnabled(OptionalTripField.DELAY),
            setCheckedState = { viewModel.setOptionalTripFieldEnabled(OptionalTripField.DELAY, it) }
        )

        CheckedText(
            iconRes = R.drawable.ic_tram,
            text = stringResource(id = R.string.label_transport_type),
            checkedFlow = viewModel.getOptionalTripFieldEnabled(OptionalTripField.TYPE),
            setCheckedState = { viewModel.setOptionalTripFieldEnabled(OptionalTripField.TYPE, it) }
        )

        CheckedText(
            iconRes = R.drawable.ic_note,
            text = stringResource(id = R.string.label_note),
            checkedFlow = viewModel.getOptionalTripFieldEnabled(OptionalTripField.NOTES),
            setCheckedState = { viewModel.setOptionalTripFieldEnabled(OptionalTripField.NOTES, it) }
        )

        Divider()
    }
}