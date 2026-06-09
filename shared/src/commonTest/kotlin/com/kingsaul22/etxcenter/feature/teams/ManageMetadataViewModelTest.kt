package com.kingsaul22.etxcenter.feature.teams

import app.cash.turbine.test
import androidx.lifecycle.viewModelScope
import com.kingsaul22.etxcenter.domain.repository.IMetadataRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ManageMetadataViewModelTest {

    @Test
    fun `metadata flow is exposed correctly`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        var viewModel: ManageMetadataViewModel? = null
        try {
            val mockRepo: IMetadataRepository = mockk()
            val expectedMetadata = mapOf(
                "current_season" to "1",
                "league_name" to "ETX League"
            )

            every { mockRepo.getMetadataFlow() } returns flowOf(expectedMetadata)

            viewModel = ManageMetadataViewModel(mockRepo)

            viewModel.metadata.test {
                assertEquals(emptyMap(), awaitItem())
                testScheduler.advanceUntilIdle()
                assertEquals(expectedMetadata, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        } finally {
            viewModel?.viewModelScope?.cancel()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `setEntry calls metadataRepository setMetadataValue`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        var viewModel: ManageMetadataViewModel? = null
        try {
            val mockRepo: IMetadataRepository = mockk()

            every { mockRepo.getMetadataFlow() } returns flowOf(emptyMap())
            coEvery { mockRepo.setMetadataValue("current_season", "2", true) } returns Result.success(Unit)

            viewModel = ManageMetadataViewModel(mockRepo)
            viewModel.setEntry("current_season", "2", true)

            testScheduler.advanceUntilIdle()

            coVerify { mockRepo.setMetadataValue("current_season", "2", true) }
        } finally {
            viewModel?.viewModelScope?.cancel()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `deleteEntry calls metadataRepository deleteMetadata`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        var viewModel: ManageMetadataViewModel? = null
        try {
            val mockRepo: IMetadataRepository = mockk()

            every { mockRepo.getMetadataFlow() } returns flowOf(emptyMap())
            coEvery { mockRepo.deleteMetadata("current_season") } returns Result.success(Unit)

            viewModel = ManageMetadataViewModel(mockRepo)
            viewModel.deleteEntry("current_season")

            testScheduler.advanceUntilIdle()

            coVerify { mockRepo.deleteMetadata("current_season") }
        } finally {
            viewModel?.viewModelScope?.cancel()
            Dispatchers.resetMain()
        }
    }
}
