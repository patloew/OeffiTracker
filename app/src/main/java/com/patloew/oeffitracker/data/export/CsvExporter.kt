package com.patloew.oeffitracker.data.export

import android.content.Context
import android.net.Uri
import android.util.Log
import com.patloew.oeffitracker.data.model.Trip
import com.patloew.oeffitracker.data.repository.TripDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
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

class CsvExporter(
    private val context: Context,
    private val tripDao: TripDao
) {

    /**
     * Exports all trips to [uri] as CSV
     *
     * @return true if export was successful
     */
    suspend fun exportTo(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
                writer.writeCsvHeader()
                tripDao.getAll().forEach { trip -> writer.writeCsvLine(trip) }
                true
            } ?: false
        } catch (e: Exception) {
            Log.e("CsvExporter", "Could not write CSV", e)
            false
        }
    }

    private fun BufferedWriter.writeCsvHeader() {
        write(""""createdDateTime","date","startCity","endCity","fare","additionalCosts","durationMinutes","delayMinutes","distanceKm","types","notes"""")
        newLine()
    }

    private fun BufferedWriter.writeCsvLine(trip: Trip) {
        write(""""${trip.formatCreatedTimeStamp()}",""")
        write(""""${trip.date}",""")
        write(""""${trip.startCity.escapeForCsv()}",""")
        write(""""${trip.endCity.escapeForCsv()}",""")
        write(""""${trip.fare?.div(100f) ?: ""}",""")
        write(""""${trip.additionalCosts?.div(100f) ?: ""}",""")
        write(""""${trip.duration?.toMinutes() ?: ""}",""")
        write(""""${trip.delay?.toMinutes() ?: ""}",""")
        write(""""${trip.distance ?: ""}",""")
        write(""""${trip.type?.joinToString(",") { it.toString().lowercase() } ?: ""}",""")
        write(""""${trip.notes?.escapeForCsv() ?: ""}"""")
        newLine()
    }

    private fun Trip.formatCreatedTimeStamp() = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(createdTimestamp), ZoneId.systemDefault())
    )

    private fun String.escapeForCsv(): String = replace("\"", "\"\"")

}