package com.kingsaul22.etxcenter.data.repository

import com.kingsaul22.etxcenter.domain.repository.IMetadataRepository
import dev.gitlive.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry

class MetadataRepositoryImpl(
    private val database: FirebaseDatabase
) : IMetadataRepository {

    override fun getMetadataFlow(): Flow<Map<String, String>> {
        return database.reference("metadata").valueEvents.map { snapshot ->
            if (!snapshot.exists) return@map emptyMap()

            snapshot.children.associate { child ->
                val key = child.key ?: ""
                val value = try {
                    child.value<Long?>()?.toString()
                } catch (e: Exception) {
                    null
                } ?: try {
                    child.value<Double?>()?.toString()
                } catch (e: Exception) {
                    null
                } ?: try {
                    child.value<String?>()
                } catch (e: Exception) {
                    null
                } ?: try {
                    child.value<Boolean?>()?.toString()
                } catch (e: Exception) {
                    null
                } ?: ""
                key to value
            }
        }
            .retry { e ->
                e.printStackTrace()
                delay(1000)
                true
            }
            .catch { e ->
                e.printStackTrace()
                emit(emptyMap())
            }
    }

    override suspend fun setMetadataValue(
        key: String,
        value: String,
        isNumeric: Boolean
    ): Result<Unit> {
        return try {
            if (isNumeric) {
                val num = value.toLongOrNull()
                    ?: return Result.failure(Exception("Invalid number: $value"))
                database.reference("metadata/$key").setValue(num)
            } else {
                database.reference("metadata/$key").setValue(value)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun deleteMetadata(key: String): Result<Unit> {
        return try {
            database.reference("metadata/$key").removeValue()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
