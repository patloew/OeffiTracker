package com.patloew.oeffitracker.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
fun SectionHeader(
    modifier: Modifier = Modifier,
    text: String,
    endText: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(1f)
        )

        if (endText != null) {
            Text(
                text = endText,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.labelMedium
            )
        }

    }

}