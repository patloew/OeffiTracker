package com.patloew.oeffitracker.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.patloew.oeffitracker.data.model.OptionalTripField
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take

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

    private val optionalTripFieldsKey = stringPreferencesKey("optionalTripFields")

    fun getOptionalTripFields(): Flow<Set<OptionalTripField>> = context.dataStore.data.map { prefs ->
        prefs[optionalTripFieldsKey]?.let { enumString ->
            enumString.takeIf { it.isNotEmpty() }?.split(',')?.map(OptionalTripField::valueOf)?.toSet()
        } ?: OptionalTripField.values().toSet()
    }

    suspend fun setOptionalTripFieldEnabled(field: OptionalTripField, enabled: Boolean) {
        getOptionalTripFields().take(1).collect { enabledFields ->
            val mutableEnabledFields = enabledFields.toMutableSet()
            if (enabled && !enabledFields.contains(field)) {
                mutableEnabledFields.add(field)
            } else if (!enabled && enabledFields.contains(field)) {
                mutableEnabledFields.remove(field)
            }
            context.dataStore.edit { prefs ->
                prefs[optionalTripFieldsKey] = mutableEnabledFields.joinToString(",")
            }
        }
    }

}