package com.patloew.oeffitracker.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
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

@Entity(indices = [Index(value = ["date"], orders = [Index.Order.DESC], name = "index_trip_date")])
@Parcelize
data class Trip(
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
    val createdTimestamp: Long,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Parcelable {
    @Ignore @IgnoredOnParcel
    val bigDecimalFare: BigDecimal? = fare?.toBigDecimal()?.divide(BigDecimal.valueOf(100))
}