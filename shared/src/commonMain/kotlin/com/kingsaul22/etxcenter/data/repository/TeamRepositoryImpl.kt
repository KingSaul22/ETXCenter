package com.kingsaul22.etxcenter.data.repository

import com.kingsaul22.etxcenter.data.dto.TeamDto
import com.kingsaul22.etxcenter.data.mapper.toDomain
import com.kingsaul22.etxcenter.domain.model.Team
import com.kingsaul22.etxcenter.domain.repository.ITeamRepository
import dev.gitlive.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TeamRepositoryImpl(
    private val database: FirebaseDatabase
) : ITeamRepository {

    override fun getTeamsFlow(): Flow<List<Team>> {
        return database.reference("teams").valueEvents.map { dataSnapshot ->
            if (!dataSnapshot.exists) return@map emptyList()

            dataSnapshot.children.mapNotNull { childSnapshot ->
                try {
                    val id = childSnapshot.key ?: return@mapNotNull null
                    val dto = childSnapshot.value<TeamDto>()
                    dto.toDomain(id = id)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }
}