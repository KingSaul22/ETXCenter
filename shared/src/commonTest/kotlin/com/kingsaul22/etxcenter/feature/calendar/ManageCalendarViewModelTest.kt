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
import kotlinx.datetime.Instant
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
    fun `calendar, teams, and matches flows are combined and exposed correctly with dynamic joins`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        var viewModel: ManageCalendarViewModel? = null
        try {
            val mockCalendarRepo: ICalendarRepository = mockk()
            val mockTeamRepo: ITeamRepository = mockk()
            val mockMatchRepo: IMatchRepository = mockk()

            // Window: 1780759000 to 1780759900
            val expectedEntries = listOf(
                CalendarEntry(
                    id = "1",
                    blueTeamId = "team_1",
                    orangeTeamId = "team_2",
                    startTime = Instant.fromEpochSeconds(1780759000),
                    endTime = Instant.fromEpochSeconds(1780759900)
                )
            )
            val expectedTeams = listOf(
                Team(id = "team_1", name = "Team One", logoUrl = null),
                Team(id = "team_2", name = "Team Two", logoUrl = null)
            )
            val expectedMatches = listOf(
                // Inside window, same teams -> should join
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
                ),
                // Outside window -> should NOT join
                MatchRecord(
                    matchId = "m2",
                    timestamp = Instant.fromEpochSeconds(1780760000),
                    blueScore = 3,
                    orangeScore = 0,
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
                ),
                // Inside window, different teams -> should NOT join
                MatchRecord(
                    matchId = "m3",
                    timestamp = Instant.fromEpochSeconds(1780759500),
                    blueScore = 1,
                    orangeScore = 0,
                    blueTeamId = "team_3",
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

                val state = awaitItem()
                assertEquals(expectedTeams, state.teams)
                assertEquals(expectedMatches, state.playedMatches)

                val entries = state.calendarEntries
                assertEquals(1, entries.size)
                // Assert it successfully auto-joined only "m1"
                assertEquals(listOf("m1"), entries[0].matchIds)

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
            coEvery { mockCalendarRepo.createCalendarEntry("team_1", "team_2", 1780759000, 1780759900) } returns Result.success(Unit)

            viewModel = ManageCalendarViewModel(mockCalendarRepo, mockTeamRepo, mockMatchRepo)
            viewModel.createCalendarEntry("team_1", "team_2", 1780759000, 1780759900)

            testScheduler.advanceUntilIdle()

            coVerify { mockCalendarRepo.createCalendarEntry("team_1", "team_2", 1780759000, 1780759900) }
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
            coEvery { mockCalendarRepo.updateCalendarEntry("1", "team_1", "team_2", 1780759000, 1780759900) } returns Result.success(Unit)

            viewModel = ManageCalendarViewModel(mockCalendarRepo, mockTeamRepo, mockMatchRepo)
            viewModel.updateCalendarEntry("1", "team_1", "team_2", 1780759000, 1780759900)

            testScheduler.advanceUntilIdle()

            coVerify { mockCalendarRepo.updateCalendarEntry("1", "team_1", "team_2", 1780759000, 1780759900) }
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
