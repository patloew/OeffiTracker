package com.patloew.oeffitracker.data.export

import android.content.Context
import android.net.Uri
import android.util.Log
import com.patloew.oeffitracker.data.export.model.v1.JsonExportV1
import com.patloew.oeffitracker.data.export.model.v1.SettingsV1
import com.patloew.oeffitracker.data.export.model.v1.TicketV1
import com.patloew.oeffitracker.data.export.model.v1.TripV1
import com.patloew.oeffitracker.data.repository.SettingsRepo
import com.patloew.oeffitracker.data.repository.TicketDao
import com.patloew.oeffitracker.data.repository.TripDao
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink

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

class JsonExporter(
    private val context: Context,
    private val tripDao: TripDao,
    private val ticketDao: TicketDao,
    private val settingsRepo: SettingsRepo,
    private val moshi: Moshi
) {

    /**
     * Exports all app data to [uri] as JSON
     *
     * @return true if export was successful
     */
    suspend fun exportTo(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openOutputStream(uri)?.sink()?.buffer()?.let(JsonWriter::of)?.use { jsonWriter ->
                val settings = SettingsV1(
                    enabledOptionalTripFields = settingsRepo.optionalTripFieldEnabledMap.filterValues { it.value }.keys,
                    includeDeductionsInProgress = settingsRepo.includeDeductionsInProgress.value
                )
                val export = JsonExportV1(
                    settings = settings,
                    trips = tripDao.getAll().map(::TripV1),
                    tickets = ticketDao.getAll().map(::TicketV1)
                )
                moshi.adapter(JsonExportV1::class.java)
                    .indent("\t")
                    .toJson(jsonWriter, export)
                true
            } ?: false
        } catch (e: Exception) {
            Log.e("JsonExporter", "Could not write JSON", e)
            false
        }
    }

}