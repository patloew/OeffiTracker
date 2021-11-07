package com.patloew.oeffitracker.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

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

class SettingsRepo(private val context: Context) {

    private val Context.dataStore by preferencesDataStore(name = "settings")

    private val highlightedTicketIdKey = longPreferencesKey("highlightedTicketId")

    fun highlightedTicketIdFlow(): Flow<Long?> =
        context.dataStore.data.map { prefs -> prefs[highlightedTicketIdKey] }

    suspend fun getHighlightedTicketId(): Long? = context.dataStore.data.first()[highlightedTicketIdKey]

    suspend fun setHighlightedTicketId(ticketId: Long?) =
        context.dataStore.edit { prefs ->
            if (ticketId != null) {
                prefs[highlightedTicketIdKey] = ticketId
            } else {
                prefs.remove(highlightedTicketIdKey)
            }
        }

}