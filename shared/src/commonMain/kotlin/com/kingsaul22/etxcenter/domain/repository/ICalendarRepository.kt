package com.kingsaul22.etxcenter.domain.repository

import com.kingsaul22.etxcenter.domain.model.CalendarEntry
import kotlinx.coroutines.flow.Flow

interface ICalendarRepository {
    fun getCalendarFlow(): Flow<List<CalendarEntry>>
    suspend fun createCalendarEntry(blueTeamId: String, orangeTeamId: String, startTime: Long, endTime: Long): Result<Unit>
    suspend fun updateCalendarEntry(id: String, blueTeamId: String, orangeTeamId: String, startTime: Long, endTime: Long): Result<Unit>
    suspend fun deleteCalendarEntry(id: String): Result<Unit>
}
