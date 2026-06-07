package com.kingsaul22.etxcenter.data.repository

import com.kingsaul22.etxcenter.data.dto.LiveStateDto
import com.kingsaul22.etxcenter.data.mapper.toDomain
import com.kingsaul22.etxcenter.domain.model.LiveState
import com.kingsaul22.etxcenter.domain.repository.ILiveRepository
import dev.gitlive.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
    }
}
