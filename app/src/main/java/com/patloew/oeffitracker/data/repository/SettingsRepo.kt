package com.patloew.oeffitracker.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class SettingsRepo(private val context: Context) {

    private val Context.dataStore by preferencesDataStore(name = "settings")

    private val highlightedTicketIdKey = longPreferencesKey("highlightedTicketId")

    fun highlightedTicketIdFlow(): Flow<Long?> =
        context.dataStore.data.map { prefs -> prefs[highlightedTicketIdKey] }

    suspend fun setHighlightedTicketId(ticketId: Long) =
        context.dataStore.edit { prefs -> prefs[highlightedTicketIdKey] = ticketId }

}