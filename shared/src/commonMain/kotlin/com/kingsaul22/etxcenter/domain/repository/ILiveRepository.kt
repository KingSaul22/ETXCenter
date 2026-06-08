package com.kingsaul22.etxcenter.domain.repository

import com.kingsaul22.etxcenter.domain.model.LiveEvent
import com.kingsaul22.etxcenter.domain.model.LiveState
import kotlinx.coroutines.flow.Flow

interface ILiveRepository {
    fun getLiveStateFlow(): Flow<LiveState?>
    fun getLiveEventsFeedFlow(): Flow<List<LiveEvent>>
}
