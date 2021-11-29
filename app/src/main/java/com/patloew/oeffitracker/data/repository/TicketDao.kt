package com.patloew.oeffitracker.data.repository

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patloew.oeffitracker.data.model.Ticket
import com.patloew.oeffitracker.data.model.TicketWithStatistics
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

    @Query("SELECT id FROM ticket WHERE date('now') BETWEEN startDate and endDate LIMIT 1")
    fun getLatestTicketId(): Flow<Long?>

    @Query("SELECT * FROM ticket WHERE (:startDate >= startDate AND :endDate <= endDate) OR (:endDate >= startDate AND :startDate < startDate) OR (:startDate <= endDate AND :endDate > endDate) LIMIT 2")
    suspend fun getFirstTwoOverlappingValidityTickets(startDate: String, endDate: String): List<Ticket>

    suspend fun getFirstTwoOverlappingValidityTickets(startDate: LocalDate, endDate: LocalDate): List<Ticket> =
        getFirstTwoOverlappingValidityTickets(
            DateTimeFormatter.ISO_DATE.format(startDate),
            DateTimeFormatter.ISO_DATE.format(endDate)
        )

    @Query(
        """
        SELECT
            ticket.id,
            ticket.name, 
            ticket.price,
            ticket.deduction,
            ticket.startDate, 
            ticket.endDate,
            ticket.createdTimestamp,
            (SELECT COALESCE(SUM(fare), 0) from trip WHERE date BETWEEN ticket.startDate and ticket.endDate) as fareSum,
            (SELECT SUM(additionalCosts) from trip WHERE date BETWEEN ticket.startDate and ticket.endDate) as additionalCostsSum,
            (SELECT SUM(duration) from trip WHERE date BETWEEN ticket.startDate and ticket.endDate) as durationSum,
            (SELECT SUM(delay) from trip WHERE date BETWEEN ticket.startDate and ticket.endDate) as delaySum,
            (SELECT SUM(distance) from trip WHERE date BETWEEN ticket.startDate and ticket.endDate) as distanceSum
        FROM ticket
        ORDER BY ticket.startDate DESC
    """
    )
    fun getTicketWithStatisticsPagingSource(): PagingSource<Int, TicketWithStatistics>
}