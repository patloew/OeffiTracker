package com.patloew.oeffitracker.ui.ticket.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.ui.PreviewTheme
import com.patloew.oeffitracker.ui.common.PriceProgressRound
import com.patloew.oeffitracker.ui.common.ProgressRoundData
import kotlinx.coroutines.flow.flowOf

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
fun TicketItem(
    ticket: TicketListData?
) {
    if (ticket != null) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val (name, dateIcon, date, priceIcon, price, progress) = createRefs()

            Text(
                modifier = Modifier
                    .constrainAs(name) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(progress.start, margin = 16.dp)
                        bottom.linkTo(dateIcon.top)
                        width = Dimension.fillToConstraints
                    },
                text = ticket.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                style = MaterialTheme.typography.body1
            )

            Icon(
                painterResource(id = R.drawable.ic_calendar),
                stringResource(id = R.string.accessibility_icon_date),
                Modifier
                    .constrainAs(dateIcon) {
                        start.linkTo(parent.start)
                        top.linkTo(name.bottom, margin = 12.dp)
                        end.linkTo(date.start)
                        bottom.linkTo(priceIcon.top)
                    }
                    .padding(bottom = 1.dp),
                tint = MaterialTheme.colors.primary
            )

            Text(
                modifier = Modifier
                    .constrainAs(date) {
                        start.linkTo(dateIcon.end, margin = 8.dp)
                        top.linkTo(dateIcon.top)
                        bottom.linkTo(dateIcon.bottom)
                        end.linkTo(progress.start, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    }
                    .padding(bottom = 2.dp),
                text = ticket.date,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.body2
            )

            Icon(
                painterResource(id = R.drawable.ic_fare),
                stringResource(id = R.string.accessibility_icon_price),
                Modifier
                    .constrainAs(priceIcon) {
                        start.linkTo(parent.start)
                        top.linkTo(dateIcon.bottom, margin = 8.dp)
                        end.linkTo(price.start)
                        bottom.linkTo(parent.bottom)
                    }
                    .padding(bottom = 1.dp),
                tint = MaterialTheme.colors.primary
            )

            Text(
                modifier = Modifier
                    .constrainAs(price) {
                        start.linkTo(priceIcon.end, margin = 8.dp)
                        top.linkTo(priceIcon.top)
                        bottom.linkTo(priceIcon.bottom)
                        end.linkTo(progress.start, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    }
                    .padding(bottom = 2.dp),
                text = ticket.price,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.body2
            )

            PriceProgressRound(
                progressData = flowOf(ticket.progressData),
                modifier = Modifier.constrainAs(progress) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
            )

        }

    } else {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val (name, dateIcon, priceIcon, progress) = createRefs()

            Text(
                modifier = Modifier
                    .constrainAs(name) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(dateIcon.top)
                    },
                text = "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                style = MaterialTheme.typography.body1
            )

            Icon(
                painterResource(id = R.drawable.ic_calendar),
                stringResource(id = R.string.accessibility_icon_date),
                Modifier
                    .constrainAs(dateIcon) {
                        start.linkTo(parent.start)
                        top.linkTo(name.bottom, margin = 12.dp)
                        bottom.linkTo(priceIcon.top)
                    }
                    .padding(bottom = 1.dp),
                tint = MaterialTheme.colors.primary
            )

            Icon(
                painterResource(id = R.drawable.ic_fare),
                stringResource(id = R.string.accessibility_icon_price),
                Modifier
                    .constrainAs(priceIcon) {
                        start.linkTo(parent.start)
                        top.linkTo(dateIcon.bottom, margin = 8.dp)
                        bottom.linkTo(parent.bottom)
                    }
                    .padding(bottom = 1.dp),
                tint = MaterialTheme.colors.primary
            )

            PriceProgressRound(
                progressData = flowOf(ProgressRoundData(0f, "")),
                modifier = Modifier.constrainAs(progress) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicketItemPreview() {
    PreviewTheme {
        Column {
            TicketItem(
                ticket = TicketListData(
                    0,
                    "KlimaTicket",
                    "100,00 €",
                    "01.01.20 - 01.01.21",
                    ProgressRoundData(0.5f, "20,5%")
                )
            )
            Divider()
            TicketItem(ticket = null)
            Divider()
        }
    }
}