package com.patloew.oeffitracker.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    positiveAction: () -> Unit,
    dismissAction: (() -> Unit)? = null
) = ActionAlertDialog(
    showAlertDialog,
    title,
    { Text(text) },
    confirmButtonText,
    confirmButtonTextColor,
    positiveAction,
    dismissAction
)

@Composable
fun ActionAlertDialog(
    showAlertDialog: MutableState<Boolean>,
    title: String,
    content: @Composable () -> Unit,
    confirmButtonText: String,
    confirmButtonTextColor: ButtonColors = ButtonDefaults.textButtonColors(),
    positiveAction: () -> Unit,
    dismissAction: (() -> Unit)? = null
) {
    if (showAlertDialog.value) {
        AlertDialog(
            onDismissRequest = { showAlertDialog.value = false },
            title = { Text(title) },
            text = content,
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
                    dismissAction?.invoke()
                }) { Text(stringResource(id = R.string.action_cancel)) }
            },
        )
    }
}