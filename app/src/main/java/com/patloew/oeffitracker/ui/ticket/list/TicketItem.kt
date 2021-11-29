package com.patloew.oeffitracker.ui.ticket.list

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import com.patloew.oeffitracker.data.model.OptionalTripField
import com.patloew.oeffitracker.ui.common.ActionAlertDialog
import com.patloew.oeffitracker.ui.common.MoreMenu
import com.patloew.oeffitracker.ui.common.PriceProgressRound
import com.patloew.oeffitracker.ui.common.ProgressRoundData
import com.patloew.oeffitracker.ui.distanceFormat
import com.patloew.oeffitracker.ui.formatDuration
import com.patloew.oeffitracker.ui.ticket.create.CreateTicketActivity
import com.patloew.oeffitracker.ui.weightFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

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
    onDelete: (Long) -> Unit,
    highlightedTicketId: Flow<Long?>,
    optionalTripFieldEnabledMap: Map<OptionalTripField, StateFlow<Boolean>>,
    data: TicketListData?
) {
    if (data != null) {
        val editTicketLauncher = rememberLauncherForActivityResult(contract = CreateTicketActivity.EditContract) { }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .clickable { editTicketLauncher.launch(data.ticket) }
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 0.dp)
        ) {
            @Composable
            fun isOptionalFieldEnabled(field: OptionalTripField): Boolean =
                optionalTripFieldEnabledMap[field]!!.collectAsState().value

            val (name, dateIcon, date, priceIcon, price, durationIcon, duration, delayIcon, delay) = createRefs()
            val (additionalCostsIcon, additionalCosts, distanceIcon, distance, co2Icon, co2) = createRefs()
            val (favIcon, favorite, progress, moreIcon) = createRefs()
            val isHighlightedTicket = highlightedTicketId.collectAsState(initial = null).value == data.ticket.id

            Text(
                modifier = Modifier
                    .constrainAs(name) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(progress.start, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    },
                text = data.ticket.name,
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
                    },
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
                    },
                text = data.validityPeriod,
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
                    },
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
                    .padding(bottom = 1.dp),
                text = data.price,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.body2
            )

            if (isOptionalFieldEnabled(OptionalTripField.ADDITIONAL_COSTS) && data.additionalCostsSum != null) {
                Icon(
                    painterResource(id = R.drawable.ic_price_plus),
                    contentDescription = null,
                    Modifier
                        .constrainAs(additionalCostsIcon) {
                            start.linkTo(parent.start)
                            top.linkTo(priceIcon.bottom, margin = 8.dp)
                        },
                    tint = MaterialTheme.colors.primary
                )

                Text(
                    modifier = Modifier
                        .constrainAs(additionalCosts) {
                            start.linkTo(additionalCostsIcon.end, margin = 8.dp)
                            top.linkTo(additionalCostsIcon.top)
                            bottom.linkTo(additionalCostsIcon.bottom)
                            end.linkTo(parent.end, margin = 16.dp)
                            width = Dimension.fillToConstraints
                        }
                        .padding(bottom = 1.dp),
                    text = stringResource(id = R.string.item_ticket_additional_costs_sum, data.additionalCostsSum),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.body2
                )
            } else {
                Spacer(
                    Modifier.constrainAs(additionalCostsIcon) {
                        start.linkTo(parent.start)
                        top.linkTo(priceIcon.bottom)
                    }
                )
            }

            if (isOptionalFieldEnabled(OptionalTripField.DURATION) && data.durationSum != null) {
                Icon(
                    painterResource(id = R.drawable.ic_clock),
                    contentDescription = null,
                    Modifier
                        .constrainAs(durationIcon) {
                            start.linkTo(parent.start)
                            top.linkTo(additionalCostsIcon.bottom, margin = 8.dp)
                        },
                    tint = MaterialTheme.colors.primary
                )

                Text(
                    modifier = Modifier
                        .constrainAs(duration) {
                            start.linkTo(durationIcon.end, margin = 8.dp)
                            top.linkTo(durationIcon.top)
                            bottom.linkTo(durationIcon.bottom)
                            end.linkTo(parent.end, margin = 16.dp)
                            width = Dimension.fillToConstraints
                        }
                        .padding(bottom = 1.dp),
                    text = stringResource(id = R.string.item_ticket_duration_sum, formatDuration(data.durationSum)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.body2
                )
            } else {
                Spacer(
                    Modifier.constrainAs(durationIcon) {
                        start.linkTo(parent.start)
                        top.linkTo(additionalCostsIcon.bottom)
                    }
                )
            }

            if (isOptionalFieldEnabled(OptionalTripField.DELAY) && data.delaySum != null) {
                Icon(
                    painterResource(id = R.drawable.ic_delay),
                    contentDescription = null,
                    Modifier
                        .constrainAs(delayIcon) {
                            start.linkTo(parent.start)
                            top.linkTo(durationIcon.bottom, margin = 8.dp)
                        },
                    tint = MaterialTheme.colors.primary
                )

                Text(
                    modifier = Modifier
                        .constrainAs(delay) {
                            start.linkTo(delayIcon.end, margin = 8.dp)
                            top.linkTo(delayIcon.top)
                            bottom.linkTo(delayIcon.bottom)
                            end.linkTo(parent.end, margin = 16.dp)
                            width = Dimension.fillToConstraints
                        }
                        .padding(bottom = 1.dp),
                    text = stringResource(id = R.string.item_ticket_delay_sum, formatDuration(data.delaySum)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.body2
                )
            } else {
                Spacer(
                    Modifier.constrainAs(delayIcon) {
                        start.linkTo(parent.start)
                        top.linkTo(durationIcon.bottom)
                    }
                )
            }

            if (isOptionalFieldEnabled(OptionalTripField.DISTANCE) && data.distanceSum != null) {
                Icon(
                    painterResource(id = R.drawable.ic_distance),
                    contentDescription = null,
                    Modifier
                        .constrainAs(distanceIcon) {
                            start.linkTo(parent.start)
                            top.linkTo(delayIcon.bottom, margin = 8.dp)
                        },
                    tint = MaterialTheme.colors.primary
                )

                Text(
                    modifier = Modifier
                        .constrainAs(distance) {
                            start.linkTo(distanceIcon.end, margin = 8.dp)
                            top.linkTo(distanceIcon.top)
                            bottom.linkTo(distanceIcon.bottom)
                            end.linkTo(parent.end, margin = 16.dp)
                            width = Dimension.fillToConstraints
                        }
                        .padding(bottom = 1.dp),
                    text = stringResource(
                        id = R.string.item_ticket_distance_sum,
                        distanceFormat.format(data.distanceSum)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.body2
                )
            } else {
                Spacer(
                    Modifier.constrainAs(distanceIcon) {
                        start.linkTo(parent.start)
                        top.linkTo(delayIcon.bottom)
                    }
                )
            }

            if (isOptionalFieldEnabled(OptionalTripField.DISTANCE) && data.co2savedSum != null) {
                Icon(
                    painterResource(id = R.drawable.ic_nature),
                    contentDescription = null,
                    Modifier
                        .constrainAs(co2Icon) {
                            start.linkTo(parent.start)
                            top.linkTo(distanceIcon.bottom, margin = 8.dp)
                        },
                    tint = MaterialTheme.colors.primary
                )

                Text(
                    modifier = Modifier
                        .constrainAs(co2) {
                            start.linkTo(co2Icon.end, margin = 8.dp)
                            top.linkTo(co2Icon.top)
                            bottom.linkTo(co2Icon.bottom)
                            end.linkTo(parent.end, margin = 16.dp)
                            width = Dimension.fillToConstraints
                        }
                        .padding(bottom = 1.dp),
                    text = stringResource(
                        id = R.string.item_ticket_co2_saved_sum,
                        weightFormat.format(data.co2savedSum)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.body2
                )
            } else {
                Spacer(
                    Modifier.constrainAs(co2Icon) {
                        start.linkTo(parent.start)
                        top.linkTo(distanceIcon.bottom)
                    }
                )
            }

            if (isHighlightedTicket) {
                Icon(
                    painterResource(id = R.drawable.ic_tram),
                    contentDescription = null,
                    Modifier
                        .constrainAs(favIcon) {
                            start.linkTo(parent.start)
                            top.linkTo(co2Icon.bottom, margin = 8.dp)
                        }
                        .size(22.dp),
                    tint = MaterialTheme.colors.primary
                )

                Text(
                    modifier = Modifier
                        .constrainAs(favorite) {
                            start.linkTo(price.start)
                            top.linkTo(favIcon.top)
                            end.linkTo(parent.end, margin = 16.dp)
                            bottom.linkTo(favIcon.bottom)
                            width = Dimension.fillToConstraints
                        }
                        .padding(bottom = 2.dp),
                    text = stringResource(id = R.string.item_ticket_favorite_hint),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.body2
                )
            }

            PriceProgressRound(
                progressData = data.progressData,
                modifier = Modifier.constrainAs(progress) {
                    top.linkTo(parent.top)
                    end.linkTo(moreIcon.start)
                    bottom.linkTo(priceIcon.bottom)
                }
            )

            val showMoreMenu = remember { mutableStateOf(false) }
            val showDeleteDialog = remember { mutableStateOf(false) }

            MoreMenu(
                modifier = Modifier.constrainAs(moreIcon) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(priceIcon.bottom)
                },
                showMoreMenu = showMoreMenu
            ) {
                DropdownMenuItem(onClick = {
                    showMoreMenu.value = false
                    showDeleteDialog.value = true
                }) { Text(stringResource(id = R.string.action_delete)) }
            }

            ActionAlertDialog(
                showAlertDialog = showDeleteDialog,
                title = stringResource(id = R.string.alert_delete_ticket_title),
                text = stringResource(
                    id = R.string.alert_delete_ticket_text,
                    data.ticket.name,
                    data.validityPeriod
                ),
                confirmButtonText = stringResource(id = R.string.action_delete),
                confirmButtonTextColor = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.error),
                positiveAction = { onDelete(data.ticket.id) }
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
                progressData = ProgressRoundData(0f, ""),
                modifier = Modifier.constrainAs(progress) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
            )
        }
    }
}