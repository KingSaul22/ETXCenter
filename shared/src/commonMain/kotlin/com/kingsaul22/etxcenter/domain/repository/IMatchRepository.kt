package com.kingsaul22.etxcenter.domain.repository

import com.kingsaul22.etxcenter.domain.model.MatchRecord
import kotlinx.coroutines.flow.Flow

interface IMatchRepository {
    fun getMatchesFlow(limit: Int = 50): Flow<List<MatchRecord>>
    
    fun getMatchByIdFlow(matchId: String): Flow<MatchRecord?>
}
