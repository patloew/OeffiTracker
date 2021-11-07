package com.patloew.oeffitracker.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
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

@Entity
data class Ticket(
    val name: String,
    val price: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdTimestamp: Long,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) {
    @Ignore
    val floatPrice: Float = price / 100f
}