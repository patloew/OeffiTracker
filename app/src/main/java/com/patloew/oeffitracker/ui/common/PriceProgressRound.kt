package com.patloew.oeffitracker.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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

data class ProgressRoundData(
    val progress: Float,
    val percentageString: String
)

@Composable
fun PriceProgressRound(
    progressData: Flow<ProgressRoundData>,
    modifier: Modifier
) {
    Box(modifier = modifier) {
        val data = progressData.collectAsState(initial = ProgressRoundData(0f, "")).value

        val h6Style = MaterialTheme.typography.h6
        var textStyle by remember { mutableStateOf(h6Style) }
        var textSizingFinished by remember { mutableStateOf(false) }

        CircularProgressIndicator(
            progress = 1f,
            color = MaterialTheme.colors.secondary,
            strokeWidth = 6.dp,
            modifier = Modifier.size(80.dp)
        )
        CircularProgressIndicator(
            progress = data.progress,
            color = MaterialTheme.colors.primary,
            strokeWidth = 6.dp,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = data.percentageString,
            style = textStyle,
            overflow = TextOverflow.Clip,
            softWrap = false,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(width = 56.dp)
                .align(Alignment.Center)
                .drawWithContent { if (textSizingFinished) drawContent() },
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.didOverflowWidth) {
                    textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.95)
                } else {
                    textSizingFinished = true
                }
            }
        )

    }
}