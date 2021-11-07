package com.patloew.oeffitracker.data.repository

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patloew.oeffitracker.data.model.Trip
import kotlinx.coroutines.flow.Flow

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
    suspend fun deleteById(tripId: Int)

    @Query("SELECT COUNT(*) FROM trip")
    fun getCount(): Flow<Int>

    @Query("SELECT SUM(fare) FROM trip WHERE date BETWEEN :startDate and :endDate")
    fun getSumOfFaresBetween(startDate: String, endDate: String): Flow<Int?>

    @Query("SELECT * FROM trip ORDER BY createdTimestamp DESC")
    fun getAllPagingSource(): PagingSource<Int, Trip>
}