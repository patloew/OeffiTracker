package com.patloew.oeffitracker.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patloew.oeffitracker.R
import kotlinx.coroutines.flow.Flow

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

data class ProgressData(
    val progress: Float,
    val percentageString: String,
    val priceString: String
)

@Composable
fun PriceProgress(
    progressDataFlow: Flow<ProgressData>,
    modifier: Modifier = Modifier,
) {
    val progressData = progressDataFlow.collectAsState(initial = ProgressData(0f, "", "")).value
    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinearProgressIndicator(
            progress = progressData.progress,
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.34f),
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        )
        Text(
            text = stringResource(
                id = R.string.progress_description,
                progressData.percentageString,
                progressData.priceString,
            ),
            style = MaterialTheme.typography.bodySmall
        )
    }
}