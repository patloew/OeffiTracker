package com.patloew.oeffitracker.ui

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.patloew.oeffitracker.ui.main.Screen
import com.patloew.oeffitracker.ui.theme.OeffiTrackerTheme
import java.text.DecimalFormat
import java.time.Duration
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
val distanceFormatShort = DecimalFormat("0.#km")
val distanceFormat = DecimalFormat("0.## km")
val weightFormat = DecimalFormat("0 kg")
val dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
val monthFormat = DateTimeFormatter.ofPattern("MMMM yyyy")

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

fun showDurationPicker(
    preSelected: Duration,
    titleText: CharSequence,
    fragmentManager: FragmentManager,
    onDurationSelected: (selectedDuration: Duration?) -> Unit
) {
    // Workaround until https://issuetracker.google.com/issues/205866514 is fixed
    val (_, hours, minutes) = preSelected.daysHoursMin
    MaterialTimePicker.Builder()
        .setTitleText(titleText)
        .setHour(hours)
        .setMinute(minutes)
        //.setHour(preSelected.toHoursPart())
        //.setMinute(preSelected.toMinutesPart())
        .setTimeFormat(TimeFormat.CLOCK_24H)
        .build()
        .apply {
            addOnPositiveButtonClickListener {
                onDurationSelected(
                    Duration.ofHours(hour.toLong()).plusMinutes(minute.toLong())
                        .takeIf { it != Duration.ZERO }
                )
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

// Workaround until https://issuetracker.google.com/issues/205866514 is fixed
private val Duration.daysHoursMin: Triple<Int, Int, Int>
    get() {
        val days = (toHours() / 24f).toInt()
        val hours = (toMinutes() / 60f).toInt().mod(24)
        val minutes = toMinutes().mod(60)
        return Triple(days, hours, minutes)
    }

// Workaround until https://issuetracker.google.com/issues/205866514 is fixed
fun formatDuration(duration: Duration): String {
    val (days, hours, minutes) = duration.daysHoursMin
    return buildString {
        var hasTextBefore = false
        if (days > 0) {
            append("$days d")
            hasTextBefore = true
        }
        if (hours > 0) {
            if (hasTextBefore) append(' ')
            append("$hours h")
            hasTextBefore = true
        }
        if (minutes > 0) {
            if (hasTextBefore) append(' ')
            append("$minutes min")
        }
    }
}

// Workaround until https://issuetracker.google.com/issues/205866514 is fixed
fun formatDurationShort(duration: Duration): String {
    val (_, hours, minutes) = duration.daysHoursMin
    return if (hours == 0) {
        "${minutes}m"
    } else {
        if (minutes > 0) {
            "${hours}h${minutes}m"
        } else {
            "${hours}h"
        }
    }
}
/*
fun formatDuration(duration: Duration) = if (duration.toHoursPart() == 0) {
    "${duration.toMinutesPart()} min"
} else {
    "${duration.toHoursPart()} h ${duration.toMinutesPart()} min"
}*/

fun amountVisualTransformation(): VisualTransformation = VisualTransformation {
    val suffix = if (it.text.isNotEmpty()) " €" else ""
    TransformedText(AnnotatedString("${it.text}$suffix"), OffsetMapping.Identity)
}