package com.patloew.oeffitracker.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patloew.oeffitracker.data.model.Trip
import com.patloew.oeffitracker.ui.PreviewTheme
import java.time.LocalDate

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
fun TripRow(trip: Trip?) {
    Text(
        modifier = Modifier.padding(16.dp),
        text = trip?.let { "${trip.startCity} -> ${trip.endCity}, ${trip.floatPrice}€, ${trip.date}" } ?: "No trip"
    )
}

@Preview(showBackground = true)
@Composable
fun TripRowPreview() {
    PreviewTheme {
        Column {
            TripRow(trip = Trip("Wien", "Graz", 2500, LocalDate.now(), System.currentTimeMillis()))
            Divider()
        }
    }
}