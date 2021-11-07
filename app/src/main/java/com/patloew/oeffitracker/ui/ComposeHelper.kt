package com.patloew.oeffitracker.ui

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.android.material.datepicker.MaterialDatePicker
import com.patloew.oeffitracker.ui.main.Screen
import com.patloew.oeffitracker.ui.theme.OeffiTrackerTheme
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

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

val percentageFormat = DecimalFormat("0.0%")
val priceFormatFloat = DecimalFormat("0.00 €")
val priceFormatInteger = DecimalFormat("0 €")
val dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)

@Composable
fun PreviewTheme(content: @Composable () -> Unit) {
    OeffiTrackerTheme {
        Scaffold(topBar = { TopAppBar(title = { Text("Öffi Tracker") }) }) {
            content()
        }
    }
}

fun NavController.navigate(screen: Screen) {
    navigate(screen.route) {
        // Pop up to the start destination of the graph to avoid building up a
        // large stack of destinations on the back stack as users select items
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}

fun showDatePicker(
    preSelected: LocalDate,
    fragmentManager: FragmentManager,
    onDateSelected: (selectedDate: LocalDate) -> Unit
) {
    MaterialDatePicker.Builder.datePicker()
        .setTitleText("")
        .setSelection(preSelected.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
        .build()
        .apply {
            addOnPositiveButtonClickListener { timeMillis ->
                onDateSelected(Instant.ofEpochMilli(timeMillis).atZone(ZoneOffset.UTC).toLocalDate())
            }
        }
        .show(fragmentManager, null)
}

/** Formats price as integer if it's ",00" */
fun formatPrice(price: Int): String {
    val formatter = if (price.mod(100) == 0) {
        priceFormatInteger
    } else {
        priceFormatFloat
    }

    return formatter.format(price / 100f)
}