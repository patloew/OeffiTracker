package com.patloew.oeffitracker.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.data.model.Trip
import com.patloew.oeffitracker.ui.PreviewTheme
import java.text.DecimalFormat
import java.time.LocalDate
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

val priceFormat = DecimalFormat("0.00 €")
val dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)

@Composable
fun TripRow(trip: Trip?) {
    if (trip != null) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 0.dp)
        ) {
            val (startIcon, startCity, endIcon, endCity, line, price, date, moreIcon) = createRefs()
            val startBarrier = createStartBarrier(date, price)

            Icon(
                Icons.Filled.Place,
                stringResource(id = R.string.accessibility_icon_place),
                Modifier
                    .constrainAs(startIcon) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
                    .padding(bottom = 1.dp),
                tint = MaterialTheme.colors.primary
            )

            Text(
                modifier = Modifier
                    .constrainAs(startCity) {
                        start.linkTo(startIcon.end, margin = 8.dp)
                        top.linkTo(startIcon.top)
                        bottom.linkTo(startIcon.bottom)
                        end.linkTo(price.start, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    }
                    .padding(bottom = 2.dp),
                text = trip.startCity,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.body1
            )

            Box(
                Modifier
                    .constrainAs(line) {
                        start.linkTo(startIcon.start)
                        top.linkTo(startIcon.bottom)
                        end.linkTo(startIcon.end)
                        bottom.linkTo(endIcon.top)
                    }
                    .size(width = 1.5.dp, height = 8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colors.onSurface)
            )

            Icon(
                painterResource(id = R.drawable.ic_flag),
                stringResource(id = R.string.accessibility_icon_place),
                Modifier
                    .constrainAs(endIcon) {
                        start.linkTo(startIcon.start)
                        top.linkTo(line.bottom)
                        bottom.linkTo(parent.bottom)
                    },
                tint = MaterialTheme.colors.primary
            )

            Text(
                modifier = Modifier
                    .constrainAs(endCity) {
                        start.linkTo(startIcon.end, margin = 8.dp)
                        top.linkTo(endIcon.top)
                        bottom.linkTo(endIcon.bottom)
                        end.linkTo(date.start, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    }
                    .padding(bottom = 2.dp),
                text = trip.endCity,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.body1
            )

            Text(
                modifier = Modifier.constrainAs(price) {
                    start.linkTo(startBarrier)
                    end.linkTo(moreIcon.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(date.top)
                },
                text = priceFormat.format(trip.floatPrice),
                maxLines = 1,
                style = MaterialTheme.typography.body1
            )

            Text(
                modifier = Modifier.constrainAs(date) {
                    start.linkTo(startBarrier)
                    end.linkTo(moreIcon.start)
                    top.linkTo(price.bottom)
                    bottom.linkTo(parent.bottom)
                },
                text = dateFormat.format(trip.date),
                maxLines = 1,
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.caption
            )

            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.constrainAs(moreIcon) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            ) {
                Icon(
                    Icons.Filled.MoreVert,
                    stringResource(id = R.string.accessibility_icon_place),
                    tint = MaterialTheme.colors.primary
                )
            }

        }
    } else {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            val (startIcon, endIcon, line) = createRefs()

            Icon(
                Icons.Filled.Place,
                stringResource(id = R.string.accessibility_icon_place),
                Modifier
                    .constrainAs(startIcon) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
                    .padding(bottom = 1.dp),
                tint = MaterialTheme.colors.primary
            )

            Box(
                Modifier
                    .constrainAs(line) {
                        start.linkTo(startIcon.start)
                        top.linkTo(startIcon.bottom)
                        end.linkTo(startIcon.end)
                        bottom.linkTo(endIcon.top)
                    }
                    .size(width = 1.5.dp, height = 6.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colors.onSurface)
            )

            Icon(
                painterResource(id = R.drawable.ic_flag),
                stringResource(id = R.string.accessibility_icon_place),
                Modifier
                    .constrainAs(endIcon) {
                        start.linkTo(startIcon.start)
                        top.linkTo(line.bottom)
                        bottom.linkTo(parent.bottom)
                    },
                tint = MaterialTheme.colors.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripRowPreview() {
    PreviewTheme {
        Column {
            TripRow(trip = Trip("Wien", "Graz", 2500, LocalDate.now(), System.currentTimeMillis()))
            Divider()
            TripRow(trip = null)
            Divider()
        }
    }
}