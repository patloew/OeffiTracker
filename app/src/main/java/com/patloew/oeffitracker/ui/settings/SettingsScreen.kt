package com.patloew.oeffitracker.ui.settings

import android.content.res.Resources
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patloew.oeffitracker.BuildConfig
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.data.model.OptionalTripField
import com.patloew.oeffitracker.ui.CreateDocumentContract
import com.patloew.oeffitracker.ui.common.ActionAlertDialog
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

private const val MIME_TYPE_CSV = "text/csv"
private const val MIME_TYPE_JSON = "application/json"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigationAction: () -> Unit,
    viewModel: SettingsViewModel,
    resources: Resources
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(id = R.string.toolbar_title_settings)) },
                navigationIcon = { NavigationBackIcon { navigationAction() } },
                scrollBehavior = scrollBehavior,
            )
        },
        content = { paddingValues ->
            SettingsContent(
                viewModel = viewModel,
                modifier = Modifier
                    .padding(paddingValues)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            )
        }
    )

    LaunchedEffect("snackbar") {
        viewModel.snackbarEvent.collect { stringRes ->
            snackbarHostState.showSnackbar(
                message = resources.getString(stringRes)
            )
        }
    }
}

@Composable
fun SettingsContent(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    val showImportDialog = remember { mutableStateOf(false) }
    val importSettings = remember { mutableStateOf(false) }

    val csvExportFileChooserLauncher = rememberLauncherForActivityResult(CreateDocumentContract()) { uri ->
        uri?.run(viewModel::exportCsv)
    }

    val jsonExportFileChooserLauncher = rememberLauncherForActivityResult(CreateDocumentContract()) { uri ->
        uri?.run(viewModel::exportJson)
    }

    val jsonImportFileChooserLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let { uri -> viewModel.importJson(uri, importSettings.value) }
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        @Composable
        fun isOptionalFieldEnabled(field: OptionalTripField): Boolean =
            viewModel.optionalTripFieldEnabledMap[field]!!.collectAsState().value

        SectionHeader(
            text = stringResource(id = R.string.section_settings_optional_trip_fields),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp)
        )

        Text(
            text = stringResource(id = R.string.settings_optional_trip_fields_description),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.67f),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        CheckedText(
            Modifier.padding(horizontal = 16.dp),
            iconRes = R.drawable.ic_price_plus,
            text = stringResource(id = R.string.label_additional_costs),
            checked = isOptionalFieldEnabled(OptionalTripField.ADDITIONAL_COSTS),
            setCheckedState = { viewModel.setOptionalTripFieldEnabled(OptionalTripField.ADDITIONAL_COSTS, it) }
        )

        CheckedText(
            Modifier.padding(horizontal = 16.dp),
            iconRes = R.drawable.ic_distance,
            text = stringResource(id = R.string.label_distance),
            checked = isOptionalFieldEnabled(OptionalTripField.DISTANCE),
            setCheckedState = { viewModel.setOptionalTripFieldEnabled(OptionalTripField.DISTANCE, it) }
        )

        CheckedText(
            Modifier.padding(horizontal = 16.dp),
            iconRes = R.drawable.ic_clock,
            text = stringResource(id = R.string.label_duration),
            checked = isOptionalFieldEnabled(OptionalTripField.DURATION),
            setCheckedState = { viewModel.setOptionalTripFieldEnabled(OptionalTripField.DURATION, it) }
        )

        CheckedText(
            Modifier.padding(horizontal = 16.dp),
            iconRes = R.drawable.ic_delay,
            text = stringResource(id = R.string.label_delay),
            checked = isOptionalFieldEnabled(OptionalTripField.DELAY),
            setCheckedState = { viewModel.setOptionalTripFieldEnabled(OptionalTripField.DELAY, it) }
        )

        CheckedText(
            Modifier.padding(horizontal = 16.dp),
            iconRes = R.drawable.ic_tram,
            text = stringResource(id = R.string.label_transport_type),
            checked = isOptionalFieldEnabled(OptionalTripField.TYPE),
            setCheckedState = { viewModel.setOptionalTripFieldEnabled(OptionalTripField.TYPE, it) }
        )

        CheckedText(
            Modifier.padding(horizontal = 16.dp),
            iconRes = R.drawable.ic_note,
            text = stringResource(id = R.string.label_note),
            checked = isOptionalFieldEnabled(OptionalTripField.NOTES),
            setCheckedState = { viewModel.setOptionalTripFieldEnabled(OptionalTripField.NOTES, it) }
        )

        Divider()

        SectionHeader(
            text = stringResource(id = R.string.section_settings_progress),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp)
        )

        CheckedText(
            Modifier.padding(horizontal = 16.dp),
            text = stringResource(id = R.string.settings_progress_description),
            checked = viewModel.includeDeductionsInProgress.collectAsState().value,
            setCheckedState = { viewModel.setIncludeDeductionsInProgress(it) }
        )

        Divider()

        SectionHeader(
            text = stringResource(id = R.string.section_settings_export),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp)
        )

        Box(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    csvExportFileChooserLauncher.launch(
                        CreateDocumentContract.Input(
                            mimeType = MIME_TYPE_CSV,
                            title = "trips.csv"
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text(stringResource(id = R.string.button_export_csv))
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            Button(
                onClick = {
                    jsonExportFileChooserLauncher.launch(
                        CreateDocumentContract.Input(
                            mimeType = MIME_TYPE_JSON,
                            title = "public_transport_data.json"
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text(stringResource(id = R.string.button_export_json))
            }
        }

        Divider()

        SectionHeader(
            text = stringResource(id = R.string.section_settings_import),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp)
        )

        Box(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    importSettings.value = false
                    showImportDialog.value = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text(stringResource(id = R.string.button_import_json))
            }
        }

        Divider()

        Text(
            text = stringResource(id = R.string.app_name) + " v" + BuildConfig.VERSION_NAME,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )


        ActionAlertDialog(
            showAlertDialog = showImportDialog,
            title = stringResource(id = R.string.alert_import_json_title),
            content = {
                Column {
                    Text(
                        stringResource(id = R.string.alert_import_json_text),
                        Modifier.padding(bottom = 16.dp)
                    )
                    CheckedText(
                        iconRes = R.drawable.ic_settings,
                        text = stringResource(id = R.string.alert_import_json_settings_import),
                        checked = importSettings.value,
                        setCheckedState = { importSettings.value = it })
                }
            },
            confirmButtonText = stringResource(id = R.string.action_choose_file),
            positiveAction = { jsonImportFileChooserLauncher.launch(arrayOf(MIME_TYPE_JSON)) }
        )
    }
}