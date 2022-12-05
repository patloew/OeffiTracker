package com.patloew.oeffitracker.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.patloew.oeffitracker.R

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
fun MoreMenu(
    modifier: Modifier,
    showMoreMenu: MutableState<Boolean>,
    dropdownMenuContent: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier) {
        IconButton(onClick = { showMoreMenu.value = true }) {
            Icon(
                Icons.Filled.MoreVert,
                stringResource(id = R.string.accessibility_icon_more),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        if (showMoreMenu.value) {
            DropdownMenu(expanded = true, onDismissRequest = { showMoreMenu.value = false }) {
                dropdownMenuContent()
            }
        }
    }
}