package com.patloew.oeffitracker.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.patloew.oeffitracker.data.model.Ticket
import com.patloew.oeffitracker.data.model.Trip
import com.patloew.oeffitracker.data.repository.ImportDao
import com.patloew.oeffitracker.data.repository.TicketDao
import com.patloew.oeffitracker.data.repository.TripDao

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

@Database(
    version = 6,
    entities = [Trip::class, Ticket::class],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
    ]
)
@TypeConverters(CustomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun ticketDao(): TicketDao
    abstract fun importDao(): ImportDao
}