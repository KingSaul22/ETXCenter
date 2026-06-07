package com.kingsaul22.etxcenter.domain.repository

import com.kingsaul22.etxcenter.domain.model.Team
import kotlinx.coroutines.flow.Flow

interface ITeamRepository {
    fun getTeamsFlow(): Flow<List<Team>>
}