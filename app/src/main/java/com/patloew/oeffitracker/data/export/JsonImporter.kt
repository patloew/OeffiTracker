package com.patloew.oeffitracker.data.export

import android.content.Context
import android.net.Uri
import android.util.Log
import com.patloew.oeffitracker.data.export.model.v1.JsonExportV1
import com.patloew.oeffitracker.data.repository.ImportDao
import com.patloew.oeffitracker.data.repository.SettingsRepo
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.source

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

class JsonImporter(
    private val context: Context,
    private val importDao: ImportDao,
    private val settingsRepo: SettingsRepo,
    private val moshi: Moshi
) {

    /**
     * Imports (and overwrites) all app data from a JSON at [uri]
     *
     * @return true if import was successful
     */
    suspend fun importFrom(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.source()?.buffer()?.let(JsonReader::of)?.use { jsonReader ->
                val jsonExport = requireNotNull(moshi.adapter(JsonExportV1::class.java).fromJson(jsonReader))
                importDao.import(jsonExport.tickets, jsonExport.trips)
                // TODO import settings
                true
            } ?: false
        } catch (e: Exception) {
            Log.e("JsonImporter", "Could not read JSON", e)
            false
        }
    }

}