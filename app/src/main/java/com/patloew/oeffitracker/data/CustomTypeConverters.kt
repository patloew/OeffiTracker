package com.patloew.oeffitracker.data

import androidx.room.TypeConverter
import com.patloew.oeffitracker.data.model.TransportType
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

object CustomTypeConverters {
    @TypeConverter
    @ToJson
    fun isoDateStringFromLocalDate(value: LocalDate?): String? =
        value?.format(DateTimeFormatter.ISO_LOCAL_DATE)

    @TypeConverter
    @FromJson
    fun isoDateStringToLocalDate(value: String?): LocalDate? =
        value?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }

    @TypeConverter
    @ToJson
    fun minutesFromDuration(value: Duration?): Long? = value?.toMinutes()

    @TypeConverter
    @FromJson
    fun minutesToDuration(value: Long?): Duration? = value?.let { Duration.ofMinutes(value) }

    @TypeConverter
    fun stringFromTransportTypeList(value: List<TransportType>?): String? = value?.joinToString(separator = ",")

    @TypeConverter
    fun stringToTransportTypeList(value: String?): List<TransportType>? =
        value?.takeIf { it.isNotEmpty() }?.split(',')?.map(TransportType::valueOf)

}