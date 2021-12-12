package com.patloew.oeffitracker.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.patloew.oeffitracker.data.model.OptionalTripField
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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
    private val includeDeductionsInProgressKey = booleanPreferencesKey("includeDeductionsInProgress")

    private val optionalTripFields: StateFlow<Set<OptionalTripField>> = context.dataStore.data.map { prefs ->
        prefs[optionalTripFieldsKey]?.let { enumString ->
            if (enumString.isEmpty()) {
                emptySet()
            } else {
                enumString.split(',').map(OptionalTripField::valueOf).toSet()
            }
        } ?: OptionalTripField.values().toSet()
    }.stateIn(GlobalScope, SharingStarted.Eagerly, emptySet())

    val optionalTripFieldEnabledMap: Map<OptionalTripField, StateFlow<Boolean>> =
        OptionalTripField.values().associateWith { field ->
            optionalTripFields.map { it.contains(field) }
                .stateIn(GlobalScope, SharingStarted.Eagerly, false)
        }

    val includeDeductionsInProgress: StateFlow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[includeDeductionsInProgressKey] ?: true
    }.stateIn(GlobalScope, SharingStarted.Eagerly, true)

    suspend fun setOptionalTripFieldEnabled(field: OptionalTripField, enabled: Boolean) {
        val enabledFields = optionalTripFields.value
        val mutableEnabledFields = enabledFields.toMutableSet()
        if (enabled && !enabledFields.contains(field)) {
            mutableEnabledFields.add(field)
        } else if (!enabled && enabledFields.contains(field)) {
            mutableEnabledFields.remove(field)
        }
        setEnabledOptionalTripFields(mutableEnabledFields)
    }

    suspend fun setEnabledOptionalTripFields(enabledFields: Set<OptionalTripField>) {
        context.dataStore.edit { prefs ->
            prefs[optionalTripFieldsKey] = enabledFields.joinToString(",")
        }
    }

    suspend fun setIncludeDeductionsInProgress(include: Boolean) {
        context.dataStore.edit { prefs -> prefs[includeDeductionsInProgressKey] = include }
    }
}