package com.patloew.oeffitracker.data.repository

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patloew.oeffitracker.data.model.Ticket
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
interface TicketDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(ticket: Ticket)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(ticket: Ticket)

    @Query("DELETE FROM ticket WHERE id = :ticketId")
    suspend fun deleteById(ticketId: Long)

    @Query("SELECT COUNT(*) FROM ticket")
    fun getCount(): Flow<Int>

    @Query("SELECT price FROM ticket WHERE id = :ticketId")
    fun getPriceById(ticketId: Long): Flow<Int>

    @Query("SELECT id FROM ticket ORDER BY startDate DESC LIMIT 1")
    fun getLatestTicketId(): Flow<Long?>

    @Query("SELECT * FROM ticket WHERE (:startDate >= startDate AND :endDate <= endDate) OR (:endDate >= startDate AND :startDate < startDate) OR (:startDate <= endDate AND :endDate > endDate) LIMIT 1")
    suspend fun getFirstOverlappingValidityTicket(startDate: String, endDate: String): Ticket?

    suspend fun getFirstOverlappingValidityTicket(startDate: LocalDate, endDate: LocalDate): Ticket? =
        getFirstOverlappingValidityTicket(
            DateTimeFormatter.ISO_DATE.format(startDate),
            DateTimeFormatter.ISO_DATE.format(endDate)
        )

    @Query("SELECT * FROM ticket ORDER BY startDate DESC")
    fun getAllPagingSource(): PagingSource<Int, Ticket>
}