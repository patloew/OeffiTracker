package com.patloew.oeffitracker.ui.ticket

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.patloew.oeffitracker.data.model.Ticket
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
fun TicketRow(
    ticket: Ticket?
) {
    if (ticket != null) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 0.dp)
        ) {

        }
    } else {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicketRowPreview() {
    PreviewTheme {
        Column {
            TicketRow(
                ticket = Ticket(
                    "Klimaticket",
                    109500,
                    LocalDate.now(),
                    LocalDate.now(),
                    System.currentTimeMillis()
                )
            )
            Divider()
            TicketRow(ticket = null)
            Divider()
        }
    }
}