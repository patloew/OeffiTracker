package com.patloew.oeffitracker.data.migration

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.core.database.getFloatOrNull
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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


/** Migrates Trip.distance from Float to Double */
object Migration5To6 : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        try {
            database.beginTransaction()
            val cursor = database.query("SELECT * FROM trip")
            while (cursor.moveToNext()) {
                cursor.getFloatOrNull(cursor.getColumnIndexOrThrow("distance"))
                    ?.let { floatDistance ->
                        val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                        val doubleDistance = floatDistance.toBigDecimal().toDouble()
                        val contentValues = ContentValues().apply { put("distance", doubleDistance) }
                        database.update("trip", SQLiteDatabase.CONFLICT_REPLACE, contentValues, "id = ?", arrayOf(id))
                    }
            }
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e("Migration5To6", "Could not migrate distance floats to double", e)
        } finally {
            database.endTransaction()
        }
    }

}