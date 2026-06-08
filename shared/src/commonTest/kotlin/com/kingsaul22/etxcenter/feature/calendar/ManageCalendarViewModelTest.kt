package com.kingsaul22.etxcenter.feature.calendar

import app.cash.turbine.test
import androidx.lifecycle.viewModelScope
import com.kingsaul22.etxcenter.domain.model.CalendarEntry
import com.kingsaul22.etxcenter.domain.model.Team
import com.kingsaul22.etxcenter.domain.model.MatchRecord
import com.kingsaul22.etxcenter.domain.repository.ICalendarRepository
import com.kingsaul22.etxcenter.domain.repository.ITeamRepository
import com.kingsaul22.etxcenter.domain.repository.IMatchRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ManageCalendarViewModelTest {

    @Test
    fun `calendar, teams, and matches flows are combined and exposed correctly`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        var viewModel: ManageCalendarViewModel? = null
        try {
            val mockCalendarRepo: ICalendarRepository = mockk()
            val mockTeamRepo: ITeamRepository = mockk()
            val mockMatchRepo: IMatchRepository = mockk()

            val expectedEntries = listOf(
                CalendarEntry(id = "1", blueTeamId = "team_1", orangeTeamId = "team_2", dateTimeWindow = "Jun 10", matchIds = listOf("m1"))
            )
            val expectedTeams = listOf(
                Team(id = "team_1", name = "Team One", logoUrl = null),
                Team(id = "team_2", name = "Team Two", logoUrl = null)
            )
            val expectedMatches = listOf(
                MatchRecord(
                    matchId = "m1",
                    timestamp = Instant.fromEpochSeconds(1780759241),
                    blueScore = 2,
                    orangeScore = 1,
                    blueTeamId = "team_1",
                    orangeTeamId = "team_2",
                    blueShots = 5,
                    blueSaves = 2,
                    blueAssists = 1,
                    blueDemos = 0,
                    orangeShots = 4,
                    orangeSaves = 3,
                    orangeAssists = 0,
                    orangeDemos = 0
                )
            )

            every { mockCalendarRepo.getCalendarFlow() } returns flowOf(expectedEntries)
            every { mockTeamRepo.getTeamsFlow() } returns flowOf(expectedTeams)
            every { mockMatchRepo.getMatchesFlow(100) } returns flowOf(expectedMatches)

            viewModel = ManageCalendarViewModel(mockCalendarRepo, mockTeamRepo, mockMatchRepo)

            viewModel.uiState.test {
                assertEquals(ManageCalendarUiState(isLoading = true), awaitItem())
                testScheduler.advanceUntilIdle()
                assertEquals(
                    ManageCalendarUiState(calendarEntries = expectedEntries, teams = expectedTeams, playedMatches = expectedMatches, isLoading = false),
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
    fun `createCalendarEntry delegates to repository`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        var viewModel: ManageCalendarViewModel? = null
        try {
            val mockCalendarRepo: ICalendarRepository = mockk()
            val mockTeamRepo: ITeamRepository = mockk()
            val mockMatchRepo: IMatchRepository = mockk()

            every { mockCalendarRepo.getCalendarFlow() } returns flowOf(emptyList())
            every { mockTeamRepo.getTeamsFlow() } returns flowOf(emptyList())
            every { mockMatchRepo.getMatchesFlow(100) } returns flowOf(emptyList())
            coEvery { mockCalendarRepo.createCalendarEntry("team_1", "team_2", "Jun 10", listOf("m1")) } returns Result.success(Unit)

            viewModel = ManageCalendarViewModel(mockCalendarRepo, mockTeamRepo, mockMatchRepo)
            viewModel.createCalendarEntry("team_1", "team_2", "Jun 10", listOf("m1"))

            testScheduler.advanceUntilIdle()

            coVerify { mockCalendarRepo.createCalendarEntry("team_1", "team_2", "Jun 10", listOf("m1")) }
        } finally {
            viewModel?.viewModelScope?.cancel()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `updateCalendarEntry delegates to repository`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        var viewModel: ManageCalendarViewModel? = null
        try {
            val mockCalendarRepo: ICalendarRepository = mockk()
            val mockTeamRepo: ITeamRepository = mockk()
            val mockMatchRepo: IMatchRepository = mockk()

            every { mockCalendarRepo.getCalendarFlow() } returns flowOf(emptyList())
            every { mockTeamRepo.getTeamsFlow() } returns flowOf(emptyList())
            every { mockMatchRepo.getMatchesFlow(100) } returns flowOf(emptyList())
            coEvery { mockCalendarRepo.updateCalendarEntry("1", "team_1", "team_2", "Jun 10", listOf("m1")) } returns Result.success(Unit)

            viewModel = ManageCalendarViewModel(mockCalendarRepo, mockTeamRepo, mockMatchRepo)
            viewModel.updateCalendarEntry("1", "team_1", "team_2", "Jun 10", listOf("m1"))

            testScheduler.advanceUntilIdle()

            coVerify { mockCalendarRepo.updateCalendarEntry("1", "team_1", "team_2", "Jun 10", listOf("m1")) }
        } finally {
            viewModel?.viewModelScope?.cancel()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `deleteCalendarEntry delegates to repository`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        var viewModel: ManageCalendarViewModel? = null
        try {
            val mockCalendarRepo: ICalendarRepository = mockk()
            val mockTeamRepo: ITeamRepository = mockk()
            val mockMatchRepo: IMatchRepository = mockk()

            every { mockCalendarRepo.getCalendarFlow() } returns flowOf(emptyList())
            every { mockTeamRepo.getTeamsFlow() } returns flowOf(emptyList())
            every { mockMatchRepo.getMatchesFlow(100) } returns flowOf(emptyList())
            coEvery { mockCalendarRepo.deleteCalendarEntry("1") } returns Result.success(Unit)

            viewModel = ManageCalendarViewModel(mockCalendarRepo, mockTeamRepo, mockMatchRepo)
            viewModel.deleteCalendarEntry("1")

            testScheduler.advanceUntilIdle()

            coVerify { mockCalendarRepo.deleteCalendarEntry("1") }
        } finally {
            viewModel?.viewModelScope?.cancel()
            Dispatchers.resetMain()
        }
    }
}
