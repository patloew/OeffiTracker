package com.patloew.oeffitracker.data.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.patloew.oeffitracker.data.model.Ticket
import com.patloew.oeffitracker.data.model.Trip

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

@Dao
abstract class ImportDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertTickets(tickets: List<Ticket>)

    @Query("DELETE FROM ticket")
    abstract suspend fun deleteAllTickets()

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertTrips(trips: List<Trip>)

    @Query("DELETE FROM trip")
    abstract suspend fun deleteAllTrips()

    @Transaction
    open suspend fun import(tickets: List<Ticket>, trips: List<Trip>) {
        deleteAllTickets()
        deleteAllTrips()
        insertTickets(tickets)
        insertTrips(trips)
    }

}