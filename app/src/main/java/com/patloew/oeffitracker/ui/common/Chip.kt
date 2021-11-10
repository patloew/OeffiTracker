package com.patloew.oeffitracker.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
fun Chip(
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(50, 50, 50, 50)
    Row(
        Modifier
            .padding(top = 8.dp, end = 8.dp)
            .clip(shape)
            .background(MaterialTheme.colors.secondary)
            .border(BorderStroke(1.dp, MaterialTheme.colors.secondaryVariant), shape = shape)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSelected) {
            Icon(
                modifier = Modifier.padding(start = 6.dp),
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = stringResource(id = R.string.accessibility_icon_selected),
                tint = MaterialTheme.colors.onSecondary
            )
        }

        Text(
            modifier = Modifier.padding(
                start = if (isSelected) 3.dp else 10.dp,
                top = 5.dp,
                end = 10.dp,
                bottom = 6.dp
            ),
            text = text,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colors.onSecondary,
            style = MaterialTheme.typography.caption
        )
    }
}