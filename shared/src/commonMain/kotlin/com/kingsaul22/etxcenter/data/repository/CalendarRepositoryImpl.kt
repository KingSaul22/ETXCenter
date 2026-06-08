package com.kingsaul22.etxcenter.data.repository

import com.kingsaul22.etxcenter.data.dto.CalendarEntryDto
import com.kingsaul22.etxcenter.data.mapper.toDomain
import com.kingsaul22.etxcenter.domain.model.CalendarEntry
import com.kingsaul22.etxcenter.domain.repository.ICalendarRepository
import dev.gitlive.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry

class CalendarRepositoryImpl(
    private val database: FirebaseDatabase
) : ICalendarRepository {

    override fun getCalendarFlow(): Flow<List<CalendarEntry>> {
        return database.reference("matches_calendar").valueEvents.map { snapshot ->
            if (!snapshot.exists) return@map emptyList()
            snapshot.children.mapNotNull { child ->
                try {
                    val id = child.key ?: return@mapNotNull null
                    child.value<CalendarEntryDto>().toDomain(id)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
        .retry { e ->
            e.printStackTrace()
            delay(1000)
            true
        }
        .catch { e ->
            e.printStackTrace()
            emit(emptyList())
        }
    }

    override suspend fun createCalendarEntry(
        blueTeamId: String,
        orangeTeamId: String,
        startTime: Long,
        endTime: Long
    ): Result<Unit> {
        return try {
            val ref = database.reference("matches_calendar").push()
            val id = ref.key ?: return Result.failure(Exception("Failed to generate key"))
            val dto = CalendarEntryDto(
                blueTeamId = blueTeamId,
                orangeTeamId = orangeTeamId,
                startTime = startTime,
                endTime = endTime
            )
            ref.setValue(dto)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun updateCalendarEntry(
        id: String,
        blueTeamId: String,
        orangeTeamId: String,
        startTime: Long,
        endTime: Long
    ): Result<Unit> {
        return try {
            val dto = CalendarEntryDto(
                blueTeamId = blueTeamId,
                orangeTeamId = orangeTeamId,
                startTime = startTime,
                endTime = endTime
            )
            database.reference("matches_calendar/$id").setValue(dto)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun deleteCalendarEntry(id: String): Result<Unit> {
        return try {
            database.reference("matches_calendar/$id").removeValue()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
