package com.kingsaul22.etxcenter.feature.teams

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
class ManageTeamsViewModelTest {

    @Test
    fun `teams and players flows are exposed correctly`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        var viewModel: ManageTeamsViewModel? = null
        try {
            val mockTeamRepo: ITeamRepository = mockk()
            val mockPlayerRepo: IPlayerRepository = mockk()

            val expectedTeams = listOf(
                Team(id = "team_1", name = "Team One", logoUrl = null),
                Team(id = "team_2", name = "Team Two", logoUrl = null)
            )
            val expectedPlayers = listOf(
                Player(id = "player_1", displayName = "Player One", teamId = "team_1"),
                Player(id = "player_2", displayName = "Player Two", teamId = null)
            )

            every { mockTeamRepo.getTeamsFlow() } returns flowOf(expectedTeams)
            every { mockPlayerRepo.getPlayersFlow() } returns flowOf(expectedPlayers)

            viewModel = ManageTeamsViewModel(mockTeamRepo, mockPlayerRepo)

            viewModel.teams.test {
                assertEquals(emptyList(), awaitItem())
                testScheduler.advanceUntilIdle()
                assertEquals(expectedTeams, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            viewModel.players.test {
                assertEquals(emptyList(), awaitItem())
                testScheduler.advanceUntilIdle()
                assertEquals(expectedPlayers, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        } finally {
            viewModel?.viewModelScope?.cancel()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `createTeam calls teamRepository createTeam`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        var viewModel: ManageTeamsViewModel? = null
        try {
            val mockTeamRepo: ITeamRepository = mockk()
            val mockPlayerRepo: IPlayerRepository = mockk()

            every { mockTeamRepo.getTeamsFlow() } returns flowOf(emptyList())
            every { mockPlayerRepo.getPlayersFlow() } returns flowOf(emptyList())
            coEvery { mockTeamRepo.createTeam("New Team", null) } returns Result.success(Unit)

            viewModel = ManageTeamsViewModel(mockTeamRepo, mockPlayerRepo)
            viewModel.createTeam("New Team")

            testScheduler.advanceUntilIdle()

            coVerify { mockTeamRepo.createTeam("New Team", null) }
        } finally {
            viewModel?.viewModelScope?.cancel()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `deleteTeam calls teamRepository deleteTeam`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        var viewModel: ManageTeamsViewModel? = null
        try {
            val mockTeamRepo: ITeamRepository = mockk()
            val mockPlayerRepo: IPlayerRepository = mockk()

            every { mockTeamRepo.getTeamsFlow() } returns flowOf(emptyList())
            every { mockPlayerRepo.getPlayersFlow() } returns flowOf(emptyList())
            coEvery { mockTeamRepo.deleteTeam("team_id") } returns Result.success(Unit)

            viewModel = ManageTeamsViewModel(mockTeamRepo, mockPlayerRepo)
            viewModel.deleteTeam("team_id")

            testScheduler.advanceUntilIdle()

            coVerify { mockTeamRepo.deleteTeam("team_id") }
        } finally {
            viewModel?.viewModelScope?.cancel()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `updateTeamRoster calls teamRepository updateTeamRoster`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        var viewModel: ManageTeamsViewModel? = null
        try {
            val mockTeamRepo: ITeamRepository = mockk()
            val mockPlayerRepo: IPlayerRepository = mockk()

            every { mockTeamRepo.getTeamsFlow() } returns flowOf(emptyList())
            every { mockPlayerRepo.getPlayersFlow() } returns flowOf(emptyList())
            coEvery { mockTeamRepo.updateTeamRoster("team_id", listOf("player_1", "player_2")) } returns Result.success(Unit)

            viewModel = ManageTeamsViewModel(mockTeamRepo, mockPlayerRepo)
            viewModel.updateTeamRoster("team_id", listOf("player_1", "player_2"))

            testScheduler.advanceUntilIdle()

            coVerify { mockTeamRepo.updateTeamRoster("team_id", listOf("player_1", "player_2")) }
        } finally {
            viewModel?.viewModelScope?.cancel()
            Dispatchers.resetMain()
        }
    }
}
