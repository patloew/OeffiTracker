package com.patloew.oeffitracker.data.repository

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.patloew.oeffitracker.data.model.Trip
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
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

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(trip: Trip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(trip: Trip)

    @Query("DELETE FROM trip WHERE id = :tripId")
    suspend fun deleteById(tripId: Long)

    @Query("SELECT COUNT(*) FROM trip")
    fun getCount(): Flow<Int>

    @Query(
        """
        SELECT 
            COALESCE(SUM(fare), 0) 
        FROM trip 
        WHERE date BETWEEN 
            (SELECT startDate FROM ticket WHERE date('now') BETWEEN startDate and endDate) 
            and (SELECT endDate FROM ticket WHERE date('now') BETWEEN startDate and endDate)
        """
    )
    fun getSumOfFaresForLatestTicket(): Flow<Long>

    @Query("SELECT COALESCE(SUM(fare), 0) FROM trip WHERE date BETWEEN :startDate and :endDate")
    suspend fun getSumOfFaresBetween(startDate: String, endDate: String): Long

    suspend fun getSumOfFaresBetween(startDate: LocalDate, endDate: LocalDate): Long =
        getSumOfFaresBetween(
            DateTimeFormatter.ISO_DATE.format(startDate),
            DateTimeFormatter.ISO_DATE.format(endDate)
        )

    @Transaction
    @Query("SELECT * FROM trip ORDER BY date DESC, createdTimestamp DESC")
    suspend fun getAll(): List<Trip>

    @Transaction
    @Query("SELECT * FROM trip ORDER BY date DESC, createdTimestamp DESC")
    fun getAllPagingSource(): PagingSource<Int, Trip>
}