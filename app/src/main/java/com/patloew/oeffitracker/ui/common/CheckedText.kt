package com.patloew.oeffitracker.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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

@Composable
fun CheckedText(
    @DrawableRes iconRes: Int,
    text: String,
    checkedFlow: Flow<Boolean>,
    setCheckedState: (Boolean) -> Unit
) {
    val checked = checkedFlow.collectAsState(initial = false).value
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .clickable { setCheckedState(!checked) }
            .padding(16.dp)

    ) {
        Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = MaterialTheme.colors.primary)
        Text(
            text = text,
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        )
        Checkbox(
            checked = checkedFlow.collectAsState(initial = false).value,
            onCheckedChange = setCheckedState,
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colors.primary)
        )
    }
}