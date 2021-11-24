package com.patloew.oeffitracker.ui.trip.list

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.patloew.oeffitracker.R
import com.patloew.oeffitracker.data.model.Trip
import com.patloew.oeffitracker.ui.common.ActionAlertDialog
import com.patloew.oeffitracker.ui.common.MoreMenu
import com.patloew.oeffitracker.ui.dateFormat
import com.patloew.oeffitracker.ui.priceFormatFloat
import com.patloew.oeffitracker.ui.trip.create.CreateTripActivity

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
fun TripItem(
    trip: Trip?,
    onDelete: (id: Long) -> Unit,
    getTemplateForToday: (Trip) -> Trip,
    getReturnTemplate: (Trip) -> Trip,
    scrollToTop: () -> Unit
) {
    if (trip != null) {
        val editTripLauncher = rememberLauncherForActivityResult(contract = CreateTripActivity.EditContract) { }
        val createFromTemplateLauncher =
            rememberLauncherForActivityResult(contract = CreateTripActivity.CreateFromTemplateContract) { created ->
                if (created) scrollToTop()
            }


        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { editTripLauncher.launch(trip) }
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 0.dp)
        ) {
            val (startIcon, startCity, endIcon, endCity, line, price, date, additionalInfos, moreIcon) = createRefs()
            val additionalInfoBarrier = createStartBarrier(price, date, additionalInfos)

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
                        end.linkTo(additionalInfoBarrier, margin = 12.dp)
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
                        end.linkTo(additionalInfoBarrier, margin = 12.dp)
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
                modifier = Modifier
                    .constrainAs(price) {
                        end.linkTo(moreIcon.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(date.top)
                    }
                    .alpha(if (trip.fare == null) 0.6f else 1f),
                text = trip.floatFare?.let(priceFormatFloat::format) ?: "? €",
                maxLines = 1,
                style = MaterialTheme.typography.body1
            )

            val typeAndDate = buildString {
                if (!trip.type.isNullOrEmpty()) {
                    if (trip.type.size > 1) {
                        append(stringResource(id = R.string.transport_type_multiple))
                    } else {
                        append(stringResource(id = trip.type.first().stringRes))
                    }
                    append(", ")
                }
                append(dateFormat.format(trip.date))
            }
            Text(
                modifier = Modifier.constrainAs(date) {
                    end.linkTo(moreIcon.start)
                    top.linkTo(price.bottom)
                    bottom.linkTo(additionalInfos.top)
                },
                text = typeAndDate,
                maxLines = 1,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.67f),
                style = MaterialTheme.typography.caption
            )

            if (trip.hasAdditionalInfos) {
                Text(
                    modifier = Modifier
                        .constrainAs(additionalInfos) {
                            end.linkTo(moreIcon.start)
                            top.linkTo(date.bottom)
                            bottom.linkTo(parent.bottom)
                        }
                        .padding(top = 1.dp),
                    text = trip.additionalInfo,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    style = MaterialTheme.typography.caption
                )
            } else {
                Spacer(modifier = Modifier.constrainAs(additionalInfos) {
                    end.linkTo(moreIcon.start)
                    top.linkTo(date.bottom)
                    bottom.linkTo(parent.bottom)
                })
            }

            val showMoreMenu = remember { mutableStateOf(false) }
            val showDeleteDialog = remember { mutableStateOf(false) }

            MoreMenu(
                modifier = Modifier.constrainAs(moreIcon) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
                showMoreMenu = showMoreMenu
            ) {
                DropdownMenuItem(onClick = {
                    showMoreMenu.value = false
                    createFromTemplateLauncher.launch(getTemplateForToday(trip))
                }) { Text(stringResource(id = R.string.action_duplicate_for_today)) }

                DropdownMenuItem(onClick = {
                    showMoreMenu.value = false
                    createFromTemplateLauncher.launch(getReturnTemplate(trip))
                }) { Text(stringResource(id = R.string.action_return_trip)) }

                DropdownMenuItem(onClick = {
                    showMoreMenu.value = false
                    showDeleteDialog.value = true
                }) { Text(stringResource(id = R.string.action_delete)) }
            }

            ActionAlertDialog(
                showAlertDialog = showDeleteDialog,
                title = stringResource(id = R.string.alert_delete_trip_title),
                text = stringResource(
                    id = R.string.alert_delete_trip_text,
                    trip.startCity,
                    trip.endCity,
                    dateFormat.format(trip.date)
                ),
                confirmButtonText = stringResource(id = R.string.action_delete),
                confirmButtonTextColor = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.error),
                positiveAction = { onDelete(trip.id) }
            )

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