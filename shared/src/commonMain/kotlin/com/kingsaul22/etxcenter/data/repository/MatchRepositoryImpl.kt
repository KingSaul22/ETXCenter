package com.kingsaul22.etxcenter.data.repository

import com.kingsaul22.etxcenter.data.dto.MatchRecordDto
import com.kingsaul22.etxcenter.data.mapper.toDomain
import com.kingsaul22.etxcenter.domain.model.MatchRecord
import com.kingsaul22.etxcenter.domain.repository.IMatchRepository
import dev.gitlive.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class MatchRepositoryImpl(
    private val database: FirebaseDatabase
) : IMatchRepository {

    override fun getMatchesFlow(limit: Int): Flow<List<MatchRecord>> {
        return database.reference("matches_index")
            .limitToLast(limit)
            .valueEvents
            .map { dataSnapshot ->
                if (!dataSnapshot.exists) return@map emptyList()

                dataSnapshot.children.mapNotNull { childSnapshot ->
                    try {
                        childSnapshot.value<MatchRecordDto>().toDomain()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }.catch { e ->
                e.printStackTrace()
                emit(emptyList())
            }
    }

    override fun getMatchByIdFlow(matchId: String): Flow<MatchRecord?> {
        return database.reference("matches_index/$matchId")
            .valueEvents
            .map { dataSnapshot ->
                if (!dataSnapshot.exists) return@map null
                try {
                    dataSnapshot.value<MatchRecordDto>().toDomain()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }.catch { e ->
                e.printStackTrace()
                emit(null)
            }
    }
}
