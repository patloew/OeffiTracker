package com.patloew.oeffitracker.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.ui.onPrimarySurface

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
fun NavigationBackIcon(navigationAction: () -> Unit) {
    Icon(
        Icons.Filled.ArrowBack,
        stringResource(id = R.string.action_back),
        tint = MaterialTheme.colors.onPrimarySurface,
        modifier = Modifier
            .padding(8.dp)
            .clip(CircleShape)
            .clickable { navigationAction() }
            .padding(8.dp)
    )
}