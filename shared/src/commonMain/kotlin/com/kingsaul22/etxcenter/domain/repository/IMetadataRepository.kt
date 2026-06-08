package com.kingsaul22.etxcenter.domain.repository

import kotlinx.coroutines.flow.Flow

interface IMetadataRepository {
    fun getMetadataFlow(): Flow<Map<String, String>>
    suspend fun setMetadataValue(key: String, value: String, isNumeric: Boolean): Result<Unit>
    suspend fun deleteMetadata(key: String): Result<Unit>
}
