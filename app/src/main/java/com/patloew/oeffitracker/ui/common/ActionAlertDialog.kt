package com.patloew.oeffitracker.ui.common

import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
fun ActionAlertDialog(
    showAlertDialog: MutableState<Boolean>,
    title: String,
    text: String,
    confirmButtonText: String,
    confirmButtonTextColor: ButtonColors = ButtonDefaults.textButtonColors(),
    positiveAction: () -> Unit
) {
    if (showAlertDialog.value) {
        AlertDialog(
            onDismissRequest = { showAlertDialog.value = false },
            title = { Text(title) },
            text = { Text(text) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showAlertDialog.value = false
                        positiveAction()
                    },
                    colors = confirmButtonTextColor
                ) { Text(confirmButtonText) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAlertDialog.value = false
                }) { Text(stringResource(id = R.string.action_cancel)) }
            },
        )
    }
}