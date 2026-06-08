package com.kingsaul22.etxcenter.feature.players

import app.cash.turbine.test
import androidx.lifecycle.viewModelScope
import com.kingsaul22.etxcenter.domain.model.Player
import com.kingsaul22.etxcenter.domain.model.Team
import com.kingsaul22.etxcenter.domain.repository.IPlayerRepository
import com.kingsaul22.etxcenter.domain.repository.ITeamRepository
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
class ManagePlayersViewModelTest {

    @Test
    fun `players and teams flows are combined and exposed correctly`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        var viewModel: ManagePlayersViewModel? = null
        try {
            val mockPlayerRepo: IPlayerRepository = mockk()
            val mockTeamRepo: ITeamRepository = mockk()

            val expectedPlayers = listOf(
                Player(id = "player_1", displayName = "Player One", teamId = "team_1"),
                Player(id = "player_2", displayName = "Player Two", teamId = null)
            )
            val expectedTeams = listOf(
                Team(id = "team_1", name = "Team One", logoUrl = null),
                Team(id = "team_2", name = "Team Two", logoUrl = null)
            )

            every { mockPlayerRepo.getPlayersFlow() } returns flowOf(expectedPlayers)
            every { mockTeamRepo.getTeamsFlow() } returns flowOf(expectedTeams)

            viewModel = ManagePlayersViewModel(mockPlayerRepo, mockTeamRepo)

            viewModel.uiState.test {
                // Initial state is loading
                assertEquals(ManagePlayersUiState(isLoading = true), awaitItem())
                testScheduler.advanceUntilIdle()
                // Emits loaded combined state
                assertEquals(
                    ManagePlayersUiState(players = expectedPlayers, teams = expectedTeams, isLoading = false),
                    awaitItem()
                )
                cancelAndIgnoreRemainingEvents()
            }
        } finally {
            viewModel?.viewModelScope?.cancel()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `createPlayer delegates to playerRepository`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        var viewModel: ManagePlayersViewModel? = null
        try {
            val mockPlayerRepo: IPlayerRepository = mockk()
            val mockTeamRepo: ITeamRepository = mockk()

            every { mockPlayerRepo.getPlayersFlow() } returns flowOf(emptyList())
            every { mockTeamRepo.getTeamsFlow() } returns flowOf(emptyList())
            coEvery { mockPlayerRepo.createPlayer("player_1", "Player One", "team_1") } returns Result.success(Unit)

            viewModel = ManagePlayersViewModel(mockPlayerRepo, mockTeamRepo)
            viewModel.createPlayer("player_1", "Player One", "team_1")

            testScheduler.advanceUntilIdle()

            coVerify { mockPlayerRepo.createPlayer("player_1", "Player One", "team_1") }
        } finally {
            viewModel?.viewModelScope?.cancel()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `updatePlayer delegates to playerRepository`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        var viewModel: ManagePlayersViewModel? = null
        try {
            val mockPlayerRepo: IPlayerRepository = mockk()
            val mockTeamRepo: ITeamRepository = mockk()

            every { mockPlayerRepo.getPlayersFlow() } returns flowOf(emptyList())
            every { mockTeamRepo.getTeamsFlow() } returns flowOf(emptyList())
            coEvery { mockPlayerRepo.updatePlayer("player_1", "Player Edit", "team_2") } returns Result.success(Unit)

            viewModel = ManagePlayersViewModel(mockPlayerRepo, mockTeamRepo)
            viewModel.updatePlayer("player_1", "Player Edit", "team_2")

            testScheduler.advanceUntilIdle()

            coVerify { mockPlayerRepo.updatePlayer("player_1", "Player Edit", "team_2") }
        } finally {
            viewModel?.viewModelScope?.cancel()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `deletePlayer delegates to playerRepository`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        var viewModel: ManagePlayersViewModel? = null
        try {
            val mockPlayerRepo: IPlayerRepository = mockk()
            val mockTeamRepo: ITeamRepository = mockk()

            every { mockPlayerRepo.getPlayersFlow() } returns flowOf(emptyList())
            every { mockTeamRepo.getTeamsFlow() } returns flowOf(emptyList())
            coEvery { mockPlayerRepo.deletePlayer("player_1") } returns Result.success(Unit)

            viewModel = ManagePlayersViewModel(mockPlayerRepo, mockTeamRepo)
            viewModel.deletePlayer("player_1")

            testScheduler.advanceUntilIdle()

            coVerify { mockPlayerRepo.deletePlayer("player_1") }
        } finally {
            viewModel?.viewModelScope?.cancel()
            Dispatchers.resetMain()
        }
    }
}
