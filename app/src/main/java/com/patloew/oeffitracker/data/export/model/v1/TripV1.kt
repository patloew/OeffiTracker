package com.patloew.oeffitracker.data.export.model.v1

import com.patloew.oeffitracker.data.model.TransportType
import com.patloew.oeffitracker.data.model.Trip
import com.squareup.moshi.JsonClass
import java.time.Duration
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

@JsonClass(generateAdapter = true)
data class TripV1(
    val startCity: String,
    val endCity: String,
    val fare: Long?,
    val additionalCosts: Long?,
    val date: LocalDate,
    val duration: Duration?,
    val delay: Duration?,
    val distance: Double?,
    val type: List<TransportType>?,
    val notes: String?,
    val createdTimestamp: Long
) {
    constructor(trip: Trip) : this(
        trip.startCity,
        trip.endCity,
        trip.fare,
        trip.additionalCosts,
        trip.date,
        trip.duration,
        trip.delay,
        trip.distance,
        trip.type,
        trip.notes,
        trip.createdTimestamp
    )

    fun toTrip(): Trip = Trip(
        startCity,
        endCity,
        fare,
        additionalCosts,
        date,
        duration,
        delay,
        distance,
        type,
        notes,
        createdTimestamp
    )
}
