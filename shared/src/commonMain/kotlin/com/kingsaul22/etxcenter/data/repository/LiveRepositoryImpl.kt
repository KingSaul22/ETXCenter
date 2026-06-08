package com.kingsaul22.etxcenter.data.repository

import com.kingsaul22.etxcenter.data.dto.LiveEventDto
import com.kingsaul22.etxcenter.data.dto.LiveStateDto
import com.kingsaul22.etxcenter.data.mapper.toDomain
import com.kingsaul22.etxcenter.domain.model.LiveEvent
import com.kingsaul22.etxcenter.domain.model.LiveState
import com.kingsaul22.etxcenter.domain.repository.ILiveRepository
import dev.gitlive.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry

class LiveRepositoryImpl(
    private val database: FirebaseDatabase
) : ILiveRepository {

    override fun getLiveStateFlow(): Flow<LiveState?> {
        return database.reference("live_state").valueEvents.map { dataSnapshot ->
            if (!dataSnapshot.exists) return@map null
            try {
                val dto = dataSnapshot.value<LiveStateDto>()
                dto.toDomain()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        .retry { e ->
            e.printStackTrace()
            delay(1000)
            true
        }
        .catch { e ->
            e.printStackTrace()
            emit(null)
        }
    }

    override fun getLiveEventsFeedFlow(): Flow<List<LiveEvent>> {
        return database.reference("live_events_feed")
            .limitToLast(20)
            .valueEvents
            .map { dataSnapshot ->
                if (!dataSnapshot.exists) return@map emptyList()

                dataSnapshot.children
                    .mapNotNull { childSnapshot ->
                        try {
                            val id = childSnapshot.key ?: return@mapNotNull null
                            val dto = childSnapshot.value<LiveEventDto>()
                            dto.toDomain(id = id)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                    .reversed()
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
}
